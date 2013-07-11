package edu.washington.cs.conf.analysis.evol;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;

/**
 * The class of recording a full execution trace
 * */
public class ExecutionTrace {

	public final List<InstructionExecInfo> history = new LinkedList<InstructionExecInfo>();
	
	public ExecutionTrace(String traceFile) {
		List<InstructionExecInfo> all = ExecutionTraceReader.createInstructionExecInfoList(traceFile);
		history.addAll(all);
	}
	
	public ExecutionTrace(String[] lines) {
		for(String line : lines) {
			history.add(ExecutionTraceReader.createInstructionExecInfo(line));
		}
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
	
	public Set<InstructionExecInfo> getExecutedInstructions(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex) {
		Utils.checkNotNull(startMethodSig);
		Utils.checkNotNull(endMethodSig);
		Utils.checkTrue(startIndex >= 0 && endIndex >= 0);
		
		Set<InstructionExecInfo> execSet = new LinkedHashSet<InstructionExecInfo>();
		
		//XXX FIXME did not consider the nested case now. may be needed in the future
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
	
	public Set<ISSABasicBlock> getExecutedBasicBlocks(
			String startMethodSig, int startIndex,
			String endMethodSig, int endIndex
			) {
		Set<ISSABasicBlock> bbSet = new HashSet<ISSABasicBlock>();
		return bbSet;
	}
}