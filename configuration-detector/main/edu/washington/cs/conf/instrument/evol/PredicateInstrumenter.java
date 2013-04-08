package edu.washington.cs.conf.instrument.evol;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Disassembler;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.analysis.Analyzer.FailureException;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;

import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Instrument predicate, method in and out
 * */
public class PredicateInstrumenter extends AbstractInstrumenter {

	static final Instruction getTracer = Util.makeGet(EfficientTracer.class, "tracer");
	static final Instruction predicateFreqTrace = Util.makeInvoke(
			EfficientTracer.class, "tracePredicateFrequency",
			new Class[] { String.class });
	static final Instruction predicateResultTrace = Util.makeInvoke(
			EfficientTracer.class, "tracePredicateResult",
			new Class[] { String.class });

	private String[] appPkgNames = null;
	private Set<String> skipClassNames = new HashSet<String>();
	
	private InstrumentSchema schema = null;

	public PredicateInstrumenter() {
		this(Collections.<String> emptyList(), Collections.<String> emptyList());
	}

	public PredicateInstrumenter(Collection<String> pkgNames,
			Collection<String> skipClass) {
		if (!pkgNames.isEmpty()) {
			this.appPkgNames = pkgNames.toArray(new String[0]);
		}
		skipClassNames.addAll(skipClass);
	}
	
	public void setInstrumentSchema(InstrumentSchema schema) {
		Utils.checkNotNull(schema);
		this.schema = schema;
	}

	@Override
	protected void doClass(ClassInstrumenter ci, Writer w) throws Exception {
		final String className = ci.getReader().getName();
		w.write("Class: " + className + "\n");
		w.flush();

		if(this.shouldSkip(className)) {
			w.write("  skip!");
			w.flush();
			return;
		}
		
		for (int m = 0; m < ci.getReader().getMethodCount(); m++) {
			MethodData d = ci.visitMethod(m);
			// d could be null, e.g., if the method is abstract or native
			if (d != null) {
				w.write("Instrumenting " + ci.getReader().getMethodName(m)
						+ " " + ci.getReader().getMethodType(m) + ":\n");
				w.flush();

				// optionally
				this.disamAndVerify(d, w);

				MethodEditor me = new MethodEditor(d);
				me.beginPass();

				final String methodSig = WALAUtils.getMethodSignature(d);
				
				// profiling the predicates
				int length = me.getInstructions().length;
				for (int i = 0; i < length; i++) {
					
					//check whether this instruction should be instrumented
					if(this.schema != null) {
						if(this.shouldSkip(methodSig, i, this.schema)) {
							continue;
						}
					}
					
					IInstruction inst = me.getInstructions()[i];
					if (this.isPredicate(inst)) {
//						   System.out.println("instr: " + inst);
							// methodSig is not a uniquely-identifiable,
							// so plus the instruction index before evaluation
							final String predSig = methodSig + EfficientTracer.SEP + i;
							me.insertBefore(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(getTracer);
									w.emit(ConstantInstruction.makeString(predSig));
									w.emit(predicateFreqTrace);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
							me.insertAfter(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(getTracer);
									w.emit(ConstantInstruction.makeString(predSig));
									w.emit(predicateResultTrace);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
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

		// if the class has been modified, issue the modified class
		if (ci.isChanged()) {
			ClassWriter cw = ci.emitClass();
			instrumenter.outputModifiedClass(ci, cw);
		}
	}

	private void disamAndVerify(MethodData d, Writer w) throws IOException,
			FailureException {
		if (disasm) {
			w.write("Initial ShrikeBT code:\n");
			(new Disassembler(d)).disassembleTo(w);
			w.flush();
		}
		if (verify) {
			Verifier v = new Verifier(d);
			v.verify();
		}
	}
	
	//FIXME it is better to double check this, and only
	//instrument predicates thare are affected by some
	//configuration options
	private boolean isPredicate(IInstruction instruction) {
		return instruction instanceof ConditionalBranchInstruction;
	}

	private boolean shouldSkip(String className) {
		if(this.appPkgNames == null || this.appPkgNames.length == 0) {
			return false;
		}
		String cName = Utils.translateSlashToDot(className);
		if(Utils.startWith(cName, this.appPkgNames)) {
			return true;
		}
		return false;
	}
	
	private boolean shouldSkip(String methodSig, int instructionIndex, InstrumentSchema schema) {
		return !schema.hasInstrumentationPredicates(methodSig, instructionIndex);
	}
}