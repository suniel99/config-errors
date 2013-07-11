package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * The execution of each instruction
 * */
public class InstructionExecInfo {

	public final String context; //the outer method sig
	public final int index;
	
	public static final int startIndex = -1;
	public static final int endIndex = 9999;
	
	private SSAInstruction ssa = null;
	
	//there are two special index, -1 => start, 9999 => end
	public InstructionExecInfo(String context, int index) {
		Utils.checkNotNull(context);
		Utils.checkTrue(startIndex>=-1 && index <= endIndex);
		this.context = context;
		this.index = index;
	}
	
	public SSAInstruction getInstruction(CGNode node) {
		if(this.ssa != null) {
			return this.ssa;
		}
		Utils.checkNotNull(node);
		Utils.checkTrue(node.getMethod().getSignature().equals(context));
		this.ssa =  WALAUtils.getInstruction(node, this.index);
		return this.ssa;
	}
	
	public boolean isStart() {
		return this.index == startIndex;
	}
	
	public boolean isEnd() {
		return this.index == endIndex;
	}
	
	public String getMethodSig() {
		return this.context;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public boolean isBranchInstruction() {
		return false;
	}
	
	public boolean evaluateToTrue() {
		throw new Error("Cannot invoke on this class, not a branch instruction.");
	}
	
	@Override
	public String toString() {
		return context + ", index: " + index;
	}
}