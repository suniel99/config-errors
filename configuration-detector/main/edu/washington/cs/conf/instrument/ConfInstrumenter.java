package edu.washington.cs.conf.instrument;

import instrument.Globals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.Disassembler;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.ClassReader;
import com.ibm.wala.shrikeCT.ClassWriter;

import edu.washington.cs.conf.util.Utils;

public class ConfInstrumenter extends AbstractInstrumenter {

	  protected boolean branch = true;
	  static final String fieldName = "_Conf_enable_trace";
	  

	  // Keep these commonly used instructions around
//	  static final Instruction getSysErr = Util.makeGet(System.class, "err");
//	  static final Instruction callPrintln = Util.makeInvoke(PrintStream.class, "println", new Class[] { String.class });
	  
	  static final Instruction getTracer = Util.makeGet(ConfTracer.class, "tracer");
	  static final Instruction callTrace = Util.makeInvoke(ConfTracer.class, "trace", new Class[] { String.class });
	  
	  private final InstrumentSchema schema;
	  
	  public ConfInstrumenter(InstrumentSchema schema) {
		  this.schema = schema;
	  }
	  
	  public ConfInstrumenter(InstrumentSchema schema, String sourceDir) {
		  this(schema);
		  if(sourceDir != null) {
			  schema.setSourceTextForAllInstrumentationPoints(sourceDir);
		  }
	  }
	  
	  @Override
	  protected void doClass(final ClassInstrumenter ci, Writer w) throws Exception {
	    final String className = ci.getReader().getName();
	    w.write("Class: " + className + "\n");
	    w.flush();

	    for (int m = 0; m < ci.getReader().getMethodCount(); m++) {
	      MethodData d = ci.visitMethod(m);
	      // d could be null, e.g., if the method is abstract or native
	      if (d != null) {
	        w.write("Instrumenting " + ci.getReader().getMethodName(m) + " " + ci.getReader().getMethodType(m) + ":\n");
	        w.flush();
	        if (disasm) {
	          w.write("Initial ShrikeBT code:\n");
	          (new Disassembler(d)).disassembleTo(w);
	          w.flush();
	        }
	        if (verify) {
	          Verifier v = new Verifier(d);
	          v.verify();
	        }

	        MethodEditor me = new MethodEditor(d);
	        me.beginPass();

        	String methodSig = this.getMethodSignature(d);
        	if(this.schema == null) {
        		continue; //go to the next method
        	}
        	Map<String, Set<Integer>> confInstPoints = schema.getInstrumentationPoints(methodSig);
        	if(confInstPoints.isEmpty()) {
        		continue;
        	}
        	Map<Integer, Set<String>> confIndices = getConfAffectedIndices(confInstPoints);
	        
	        //profiling the predicates
	        if(branch) {
	        	int length = me.getInstructions().length;
	        	for(int i = 0; i < length; i++) {
	        		IInstruction inst = me.getInstructions()[i];
	        		if(confIndices.containsKey(i)) {
	        			System.out.println("inst: " + inst);
	        			for(String conf : confIndices.get(i)) {
	        				
	        				//FIXME expensive operations
//	        				int lineNumber = schema.getSourceLineNumber(conf, methodSig, i);
//	        				String sourceTxt = schema.getSourceCodeText(conf, methodSig, i);
	        				
	        				final String msg0 = PRE + SEP + conf + SEP + /*lineNumber + SUB_SEP + sourceTxt + SUB_SEP +*/ methodSig + "_index_" + i; //FIXME methodSig is not a distinguisable sig
		        			final String msg1 = POST + SEP + conf + SEP + /*+ lineNumber + SUB_SEP + sourceTxt + SUB_SEP +*/ methodSig + "_index_" + i;
		            		me.insertBefore(i, new MethodEditor.Patch() {
		                      @Override
		                      public void emitTo(MethodEditor.Output w) {
		                        //w.emit(getSysErr);
		                    	w.emit(getTracer);
		                        w.emit(ConstantInstruction.makeString(msg0));
		                        w.emit(callTrace);
		                        //w.emit(callPrintln);
		                        InstrumentStats.addInsertedInstructions(1);
		                      }
		                   });
		            	   me.insertAfter(i, new MethodEditor.Patch() {
		                        @Override
		                        public void emitTo(MethodEditor.Output w) {
		                          //w.emit(getSysErr);
		                          w.emit(getTracer);
		                          w.emit(ConstantInstruction.makeString(msg1));
		                          w.emit(callTrace);
		                          //w.emit(callPrintln);
		                          InstrumentStats.addInsertedInstructions(1);
		                        }
		                   });
	        			}
	        		}

	        	}
	        }
	        // this updates the data d
	        me.applyPatches();
	        if (disasm) {
	          w.write("Final ShrikeBT code:\n");
	          (new Disassembler(d)).disassembleTo(w);
	          w.write(Globals.lineSep);
	          w.flush();
	        }
	      }
	    }

	    if (ci.isChanged()) {
	      ClassWriter cw = ci.emitClass();
	      cw.addField(ClassReader.ACC_PUBLIC | ClassReader.ACC_STATIC, fieldName, Constants.TYPE_boolean, new ClassWriter.Element[0]);
	      instrumenter.outputModifiedClass(ci, cw);
	    }
	  }
	  
	  private Map<Integer, Set<String>> getConfAffectedIndices(Map<String, Set<Integer>> confMap) {
		  Map<Integer, Set<String>> ret = new LinkedHashMap<Integer, Set<String>>();
		  for(String conf : confMap.keySet()) {
			  for(Integer i : confMap.get(conf)) {
				  if(!ret.containsKey(i)) {
					  ret.put(i, new LinkedHashSet<String>());
				  }
				  ret.get(i).add(conf);
			  }
		  }
		  return ret;
	  }
	  
}
