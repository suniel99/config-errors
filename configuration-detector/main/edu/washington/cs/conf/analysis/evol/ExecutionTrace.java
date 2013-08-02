package edu.washington.cs.conf.analysis.evol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * The class of recording a full execution trace
 * */
//should make it lazily-initialized, the trace file can be extremely large,
//do not read it all at once
public class ExecutionTrace {
	//for experiment use only
	private final List<InstructionExecInfo> history = new LinkedList<InstructionExecInfo>();
	
	private final String traceFile;
	//the sig map file records a number to the instruction signature
	private final String sigmapFile;
	private final String predicateFile;
	
	public ExecutionTrace(String traceFile, String sigmapFile, String predicateFile) {
		Utils.checkFileExistence(traceFile);
		Utils.checkFileExistence(sigmapFile);
		Utils.checkFileExistence(predicateFile);
		this.traceFile = traceFile;
		this.sigmapFile = sigmapFile;
		this.predicateFile = predicateFile;
	}
	
	/**
	 * This constructor is only for experimental testing purpose
	 * */
	ExecutionTrace(String[] lines) {
		for(String line : lines) {
			history.add(ExecutionTraceReader.createInstructionExecInfo(line));
		}
		this.traceFile = null;
		this.sigmapFile = null;
		this.predicateFile = null;
	}
	
	public Set<PredicateExecInfo> getExecutedPredicates() {
		Collection<PredicateExecInfo> predColl = ExecutionTraceReader.createPredicateExecInfoList(this.predicateFile,this.sigmapFile);
		Set<PredicateExecInfo> predSet = new LinkedHashSet<PredicateExecInfo>(predColl);
		return predSet;
	}
	
	public InstructionExecInfo getImmediatePostDominator(CodeAnalyzer coder, PredicateExecInfo pred) {
		String methodSig = pred.getMethodSig();
		int index = pred.getIndex();
		CGNode node = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), methodSig);
		if(node == null) {
			System.err.println("Missing node for method sig: " + methodSig);
			return null;
		}
//		Utils.checkNotNull(node.getIR(), "node sig: " + node.getMethod().getSignature()
//				+ ", is abstract method? " + node.getMethod().isAbstract());
		SSAInstruction ssa = WALAUtils.getInstruction(node, index);
		SSAInstruction domSSA = PostDominatorFinder.getImmediatePostDominatorInstruction(node, ssa);
		
		//represent at the end of a method
		if(domSSA == null) {
			return InstructionExecInfo.createMethodEndExec(methodSig);
		}
		
		//get the index
		int domSSAIndex = WALAUtils.getInstructionIndex(node, domSSA);
		
		InstructionExecInfo info = new InstructionExecInfo(methodSig, domSSAIndex);
		info.setSSAInstruction(domSSA);
		info.setCGNode(node);
		
		//the executed instruction info
		return info;
	}
	
	public Set<InstructionExecInfo> getExecutedInstructionsInPredicate(CodeAnalyzer coder, PredicateExecInfo pred) {
		InstructionExecInfo postDom = this.getImmediatePostDominator(coder, pred);
		return this.getExecutedInstructions(pred.getMethodSig(), pred.getIndex(),
				postDom.getMethodSig(), postDom.getIndex());
	}
	
	
	//FIXME
	//given an execute predicate, and its execution frequency.
	//return the delta instructions that have been executed
	
	/**
	 * need to exclude predicate with similar behaviors,
	 * e.g., off-by-one
	 * */
	/**
	 * avoid double count in the recursive case,
	 * foo() {
	 *   if(x) {
	 *      foo();
	 *   }
	 * }
	 * the trace would be like:  enter-x enter-x exit-x exit-x
	 * */
	public Set<SSAInstruction> getExecutedInstructions(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex,
			CodeAnalyzer coder) {
		Set<InstructionExecInfo> execSet
		    = this.getExecutedInstructions(startMethodSig, startIndex, endMethodSig, endIndex);
		Set<SSAInstruction> ssaSet = new LinkedHashSet<SSAInstruction>();
		for(InstructionExecInfo exec : execSet) {
			ssaSet.add(coder.getInstruction(exec.getMethodSig(), exec.getIndex()));
		}
		return ssaSet;
	}
	
	//FIXME did not consider the nested case now. may be needed in the future
	//the trace file cotent looks like: NORMAL:a_number
	//the number correspond to the sig map
	public Set<InstructionExecInfo> getExecutedInstructions(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex) {
		//if the history has already been processed, then use the full processed history
		if(!this.history.isEmpty()) {
			return this.getExecutedInstructions_internal(startMethodSig, startIndex,
					endMethodSig, endIndex);
		}
		//the return set
		Set<InstructionExecInfo> returnSet = new LinkedHashSet<InstructionExecInfo>();
		//read the file directly to save memory
		boolean start = false;
		Map<Integer, String> sigMap = SigMapParser.parseSigNumMapping(this.sigmapFile);
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(this.traceFile)));  
			String line = null; 
		    while ((line = br.readLine()) != null) {  
		       line = line.trim();
		       String[] splits = line.split(EfficientTracer.EVAL_SEP);
		       Utils.checkTrue(splits.length == 2, "error: " + line);
		       String instrSig = sigMap.get(Integer.parseInt(splits[1]));
		       Utils.checkNotNull(instrSig);
		       line = splits[0] + EfficientTracer.EVAL_SEP + instrSig;
		       if(ExecutionTraceReader.checkInstruction(line, startMethodSig, startIndex)) {
		    	   if(start) {
		    		   Utils.fail("unsupported, already started.");
		    	   } else {
		    		   start = true;
		    	   }
		       } else if (ExecutionTraceReader.checkInstruction(line, endMethodSig, endIndex)) {
		    	   if(start) {
		    		   start = false;
		    	   } else {
		    		   Utils.fail("unsupported, not started yet.");
		    	   }
		       } else {
		    	   if(start) {
		    		   
		    	   }
		       }
		    }
		} catch (Throwable e) {
			throw new Error(e);
		}
		return returnSet;
	}
	
	/**
	 * This is just for testing purpose
	 * */
	private Set<InstructionExecInfo> getExecutedInstructions_internal(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex) {
		Utils.checkNotNull(startMethodSig);
		Utils.checkNotNull(endMethodSig);
		Utils.checkTrue(startIndex >= 0 && endIndex >= 0);
		//the return set
		Set<InstructionExecInfo> execSet = new LinkedHashSet<InstructionExecInfo>();
		
		boolean start = false;
		for(InstructionExecInfo execInfo : this.history) {
			String methodSig = execInfo.getMethodSig();
			int index = execInfo.getIndex();
			if(methodSig.equals(startMethodSig) && index == startIndex) {
				if(start) {
					Utils.fail("ERROR: meet nested cases.");
				} else {
					start = true;
				}
			} else if (methodSig.equals(endMethodSig) && index == endIndex) {
				if(start) {
					start = false;
				} else {
					Utils.fail("Error: did not start yet.");
				}
			} else {
				//depends on the flag
				if(start) {
					execSet.add(execInfo);
				}
			}
		}
		
		return execSet;
	}
}
