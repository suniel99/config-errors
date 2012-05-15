package edu.washington.cs.conf.analysis;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.NormalReturnCaller;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement.Kind;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileProvider;

import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class SlicingHelper {
	
	public enum CG {RTA, ZeroCFA, ZeroContainerCFA, VanillaZeroOneCFA, ZeroOneCFA, ZeroOneContainerCFA, OneCFA, TwoCFA, CFA, TempZeroCFA}
	
	public final String classPath;
	public final String mainClass;
	
	private CG type = CG.ZeroOneCFA;
	private String byPassFile = null;
	private int cfaprecision = -1;
	private String exclusionFile = CallGraphTestUtil.REGRESSION_EXCLUSIONS;
	private boolean contextSensitive = true;
	private DataDependenceOptions dataOption = DataDependenceOptions.NO_BASE_PTRS;
	private ControlDependenceOptions controlOption =ControlDependenceOptions.NONE;
	//the CISlicer
 	private CISlicer slicer = null;
	
	private AnalysisScope scope = null;
	private ClassHierarchy cha = null;
	private Iterable<Entrypoint> entrypoints = null;
	private CallGraphBuilder builder = null;
	private AnalysisOptions options = null;
	private CallGraph cg = null;
	
	public SlicingHelper(String classPath, String mainClass) {
		this.classPath = classPath;
		this.mainClass = mainClass;
	}
	
	public void buildAnalysis() {
		try {
		  System.out.println("Using exclusion file: " + this.exclusionFile);
		  System.out.println("CG type: " + this.type);
		  
	      this.scope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(this.classPath, (new FileProvider())
	          .getFile(exclusionFile));
	      this.cha = ClassHierarchy.make(this.scope);
	      this.entrypoints = com.ibm.wala.ipa.callgraph.impl.Util.makeMainEntrypoints(scope, cha, mainClass);
	      this.options = CallGraphTestUtil.makeAnalysisOptions(scope, entrypoints);
	      this.builder = chooseCallGraphBuilder(options, new AnalysisCache(), cha, scope);
	      //build the call graph
	      System.out.println("Building call graph...");
	      System.out.println("Number of entry points: " + Utils.countIterable(this.entrypoints));
	      this.cg = this.builder.makeCallGraph(options, null);
	      System.err.println(CallGraphStats.getStats(this.cg));
		} catch (Throwable e) {
			throw new Error(e);
		}
	}
	
	public void setCGType(CG type) {
		this.type = type;
	}
	
	public void setExclusionFile(String fileName) {
		this.exclusionFile = fileName;
	}
	
	public void setContextSensitive(boolean cs) {
		this.contextSensitive = cs;
	}
	
	public void setDataDependenceOptions(DataDependenceOptions op) {
		this.dataOption = op;
	}
	
	public void setControlDependenceOptions(ControlDependenceOptions op) {
		this.controlOption = op;
	}
	
	public ClassHierarchy getClassHierarchy() {
		return this.cha;
	}
	
	public CallGraph getCallGraph() {
		return this.cg;
	}
	
	public PointerAnalysis getPointerAnalysis() {
		return this.builder.getPointerAnalysis();
	}
	
	public ConfPropOutput outputSliceConfOption(ConfEntity entity) {
		Collection<Statement> stmts = sliceConfOption(entity);
		Collection<IRStatement> irs = convert(stmts);
		ConfPropOutput output = new ConfPropOutput(entity, irs);
		return output;
	}
	
	public Collection<Statement> sliceConfOption(ConfEntity entity) {
		checkCG();
		Statement s = this.extractConfStatement(entity);
		Utils.checkNotNull(s, "statement is null? " + entity);
		//compute the slice
		try {
			if(this.contextSensitive) {
			    return computeContextSensitiveForwardSlice(s);
			} else {
				return this.computeContextInsensitiveForwardThinSlice(s);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public Collection<Statement> computeContextSensitiveForwardSlice(Statement seed) throws IllegalArgumentException, CancelException {
		return computeConetxtSensitiveSlice(seed, false, this.dataOption, this.controlOption);
	}
	
	public Collection<Statement> computeConetxtSensitiveSlice(Statement seed, boolean goBackward,
			DataDependenceOptions dOptions, ControlDependenceOptions cOptions) throws IllegalArgumentException, CancelException {
		  checkCG();
	      System.err.println("Seed statement in context-sensitive slicing: " + seed);
	      System.err.println("Data dependence option: " + dOptions);
	      System.err.println("Control dependence option: " + cOptions);
	      // compute the slice as a collection of statements
	      Collection<Statement> slice = null;
	      if (goBackward) {
	        slice = Slicer.computeBackwardSlice(seed, cg, builder.getPointerAnalysis(), dOptions, cOptions);
	      } else {
	        // for forward slices ... we actually slice from the return value of calls.
	        seed = getReturnStatementForCall(seed);
	        slice = Slicer.computeForwardSlice(seed, cg, builder.getPointerAnalysis(), dOptions, cOptions);
	      }
	      //SlicerTest.dumpSlice(slice);
	      return slice;
	}
	
	public Collection<Statement> computeContextInsensitiveForwardThinSlice(Statement seed) throws IllegalArgumentException, CancelException {
		return computeConetxtInsensitiveThinSlice(seed, false, this.dataOption, this.controlOption);
	}
	
	public Collection<Statement> computeConetxtInsensitiveThinSlice(Statement seed, boolean goBackward,
			DataDependenceOptions dOptions, ControlDependenceOptions cOptions) throws IllegalArgumentException, CancelException {
		checkCG();
		System.err.println("Seed statement in context-insensitive slicing: " + seed);
		System.err.println("Data dependence option: " + dOptions);
	    System.err.println("Control dependence option: " + cOptions);
		
		//initialize the slice
		if(slicer == null) {
		    slicer = new CISlicer(cg, builder.getPointerAnalysis(), dOptions, cOptions);
		}
		
		Collection<Statement> slice = null;
		if (goBackward) {
	        slice = slicer.computeBackwardThinSlice(seed);
	      } else {
	        // for forward slices ... we actually slice from the return value of calls.
	        seed = getReturnStatementForCall(seed);
	        slice = slicer.computeForwardThinSlice(seed);
	      }
		
		return slice;
	}
	
	private void checkCG() {
		if(this.cg == null) {
			  throw new RuntimeException("Please call buildAnalysis() first.");
		  }
	}
	
	Statement extractConfStatement(ConfEntity entity) {
		String className = entity.getClassName();
		String confName = entity.getConfName();
		String assignMethod = entity.getAssignMethod(); //FIXME we may need more specific method signature
		boolean isStatic = entity.isStatic();
		
		String targetMethod = assignMethod != null
		     ? assignMethod
		     : (isStatic ? "<clinit>" : "<init>");
		
		//String irSig = isStatic ? "putstatic" : "putfield";
		//SSAPutInstruction.class;
		
		Log.logln("target method name: " + targetMethod);
		
		for(CGNode node : cg) {
			String fullMethodName = WALAUtils.getFullMethodName(node.getMethod());
			Log.logln("full method name: " + fullMethodName + ",  className: " + className);
			if(fullMethodName.equals(className + "." + targetMethod)) {
				Iterator<SSAInstruction> ssaIt = node.getIR().iterateAllInstructions();
				while(ssaIt.hasNext()) {
					SSAInstruction inst = ssaIt.next();
					if(inst instanceof SSAPutInstruction) {
						Log.logln("In method: " + fullMethodName + "put inst: " + inst);
						SSAPutInstruction putInst = (SSAPutInstruction)inst;
						if(putInst.isStatic() == isStatic) {
							//FIXME ugly below
							Log.logln("put inst with same modifier: " + putInst);
							if(putInst.toString().indexOf(confName) != -1) {
								//find it
								Statement s = new NormalStatement(node, WALAUtils.getInstructionIndex(node, inst));
								return s;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	private CallGraphBuilder chooseCallGraphBuilder(AnalysisOptions options, AnalysisCache cache,
	      IClassHierarchy cha, AnalysisScope scope) {
	    CallGraphBuilder builder = null;
	    if(this.type == CG.ZeroCFA) {
			System.out.println("Using 0-CFA call graph");
			builder = Util.makeZeroCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.ZeroOneCFA) {
			System.out.println("Using 0-1-CFA call graph");
			builder = Util.makeVanillaZeroOneCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.ZeroContainerCFA) {
			System.out.println("Using 0-container-CFA call graph");
			builder = Util.makeVanillaZeroOneContainerCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.RTA) {
			System.out.println("Using RTA call graph");
			builder = Util.makeRTABuilder(options, cache, cha, scope);
		} else if (this.type == CG.ZeroOneContainerCFA) {
			System.out.println("Using 0-1-container-CFA call graph");
			builder = Util.makeZeroOneContainerCFABuilder(options, cache, cha, scope);
		} else if (this.type == CG.OneCFA) {
			System.out.println("Using 1-CFA call graph");
			builder = WALAUtils.makeOneCFABuilder(options,  cache, cha, scope);
		} else if (this.type == CG.TwoCFA) {
			System.out.println("Using 2-CFA call graph");
			builder = WALAUtils.makeCFABuilder(2, options,  cache, cha, scope);
		} else if (this.type == CG.CFA) { 
			if(this.cfaprecision < 2) {
			throw new RuntimeException("Please set cfa precision first.");
			}
			System.out.println("Use CFA with precision: " + this.cfaprecision);
			builder = WALAUtils.makeCFABuilder(this.cfaprecision, options,  cache, cha, scope);
		} else if (this.type == CG.TempZeroCFA) {
			System.out.println("Use Temp-0-CFA with 0 context precision ");
			builder = WALAUtils.makeCFABuilder(0, options, cache, cha, scope);
		}else {
			throw new RuntimeException("The CG type: " + type + " is unknonw");
		}
		assert builder != null;
		
		//add the bypass file
		if(this.byPassFile != null) {
			System.err.println("Use bypass file: " + this.byPassFile);
			Util.addBypassLogic(options, scope, Utils.class.getClassLoader(), this.byPassFile, cha);
		}
		
		return builder;
	}
	
	public static Statement getReturnStatementForCall(Statement s) {
	    if (s.getKind() == Kind.NORMAL) {
	      NormalStatement n = (NormalStatement) s;
	      SSAInstruction st = n.getInstruction();
	      if (st instanceof SSAInvokeInstruction) {
	        SSAAbstractInvokeInstruction call = (SSAAbstractInvokeInstruction) st;
	        if (call.getCallSite().getDeclaredTarget().getReturnType().equals(TypeReference.Void)) {
	          throw new IllegalArgumentException("this driver computes forward slices from the return value of calls.\n" + ""
	              + "Method " + call.getCallSite().getDeclaredTarget().getSignature() + " returns void.");
	        }
	        System.err.println("Use return value as slicing seed: " + s);
	        return new NormalReturnCaller(s.getNode(), n.getInstructionIndex());
	      } else {
	        return s;
	      }
	    } else {
	      return s;
	    }
	  }
	
	public static Collection<IRStatement> convert(Collection<Statement> stmts) {
		Collection<IRStatement> irs = new LinkedList<IRStatement>();
		
		for(Statement s : stmts) {
			if(s instanceof StatementWithInstructionIndex) {
				if(s.getNode().getMethod() instanceof ShrikeBTMethod) {
				    IRStatement ir = new IRStatement((StatementWithInstructionIndex)s);
				    irs.add(ir);
			    } else {
			    	//skip fake method 
			    	Log.logln("skip stmt: " + s + " in method: " + s.getNode().getClass());
			    }
			} else {
				Log.logln("skip non-StatementWithInstructionIndex: " + s);
			}
		}
		
		return irs;
	}
}