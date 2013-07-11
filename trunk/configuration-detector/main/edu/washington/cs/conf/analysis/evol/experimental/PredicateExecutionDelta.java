package edu.washington.cs.conf.analysis.evol.experimental;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.CodeAnalysisUtils;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

@Deprecated
public class PredicateExecutionDelta {

	public final CGNode oldNode;
	public final int oldIndex;
	
	public final CGNode newNode;
	public final int newIndex;
	
	private int oldExecFreq = 0;
	private int oldEvalTrue = 0;
	
	private int newExecFreq = 0;
	private int newEvalTrue = 0;
	
	private SSAInstruction oldSSA = null;
	private SSAInstruction newSSA = null;
	
	public static PredicateExecutionDelta createExecutionDelta(CGNode oldNode, int oldIndex,
			CGNode newNode, int newIndex) {
		Utils.checkNotNull(oldNode);
		Utils.checkNotNull(newNode);
		return new PredicateExecutionDelta(oldNode, oldIndex, newNode, newIndex);
	}
	
	public static PredicateExecutionDelta createOldExecutionDelta(CGNode oldNode, int oldIndex) {
		Utils.checkNotNull(oldNode);
		return new PredicateExecutionDelta(oldNode, oldIndex, null, -1);
	}
	
	public static PredicateExecutionDelta createNewExecutionDelta(CGNode newNode, int newIndex) {
		Utils.checkNotNull(newNode);
		return new PredicateExecutionDelta(null, -1, newNode, newIndex);
	}
	
	private PredicateExecutionDelta(CGNode oldNode, int oldIndex,
			CGNode newNode, int newIndex) {
		this.oldNode = oldNode;
		this.oldIndex = oldIndex;
		this.newNode = newNode;
		this.newIndex = newIndex;
		if(this.oldNode != null) {
			Utils.checkTrue(oldIndex >= 0 && oldIndex < oldNode.getIR().getInstructions().length);
			this.oldSSA = WALAUtils.getInstruction(oldNode, oldIndex);
			Utils.checkTrue(CodeAnalysisUtils.isPredicateInstruction(this.oldSSA));
		}
		if(this.newNode != null) {
			Utils.checkTrue(newIndex >= 0 && newIndex < newNode.getIR().getInstructions().length);
			this.newSSA = WALAUtils.getInstruction(newNode, newIndex);
			Utils.checkTrue(CodeAnalysisUtils.isPredicateInstruction(this.newSSA));
		}
	}
	
	public int computeCost() {
		throw new Error();
	}
	
	//in total due to the different branch it takes
	//for example, a branch is taken 4 times in old version, but 3 times in the new
	//version. what is the total delta instructions
	private int getTotalDeltaInstructionNumber() {
		throw new Error();
	}
	
	public int getOldExecFreq() {
		return oldExecFreq;
	}

	public void setOldExecFreq(int oldExecFreq) {
		Utils.checkTrue(oldExecFreq >= 0);
		this.oldExecFreq = oldExecFreq;
	}

	public int getOldEvalTrue() {
		return oldEvalTrue;
	}

	public void setOldEvalTrue(int oldEvalTrue) {
		Utils.checkTrue(oldEvalTrue >= 0);
		this.oldEvalTrue = oldEvalTrue;
	}

	public int getNewExecFreq() {
		return newExecFreq;
	}

	public void setNewExecFreq(int newExecFreq) {
		Utils.checkTrue(newExecFreq >= 0);
		this.newExecFreq = newExecFreq;
	}

	public int getNewEvalTrue() {
		return newEvalTrue;
	}

	public void setNewEvalTrue(int newEvalTrue) {
		Utils.checkTrue(newEvalTrue >= 0);
		this.newEvalTrue = newEvalTrue;
	}
}