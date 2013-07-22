package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class PredicateExecution {

	/**
	 * If the predicate is executed on both versions, use
	 * the methodSig and index on the new version here.
	 * */
	public final String methodSig;
	public final int index;
	
	/**
	 * If the ssa executes on both versions, use the ssa / node on the
	 * new version here.
	 * */
	private SSAInstruction ssa = null;
	private CGNode node = null;
	
	private int execFreqInOld = -1;
	private int evalResultInOld = -1;
	private int execFreqInNew = -1;
	private int evalResultInNew = -1;
	
	private int monitoredExec = -1;
	private int monitoredEval = -1;
	
	public PredicateExecution(String methodSig, int index) {
		Utils.checkNotNull(methodSig);
		Utils.checkTrue(index >= 0);
		this.methodSig = methodSig;
		this.index = index;
	}
	
	public SSAInstruction getInstruction(CodeAnalyzer coder) {
		if(this.ssa != null) {
			return this.ssa;
		}
		this.node = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), this.methodSig);
		this.ssa = WALAUtils.getInstruction(this.node, index);
		return this.ssa;
	}
	
	public CGNode getNode(CodeAnalyzer coder) {
		if(this.node != null) {
			return this.node;
		}
		this.node = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), this.methodSig);
		return this.node;
	}
	
	public void setOldExecutionInfo(int oldFreq, int oldResult) {
		Utils.checkTrue(oldFreq >= 0);
		Utils.checkTrue(oldResult >= 0);
		Utils.checkTrue(oldFreq >= oldResult);
		this.execFreqInOld = oldFreq;
		this.evalResultInOld = oldResult;
	}
	
	public void setNewExecutionInfo(int newFreq, int newResult) {
		Utils.checkTrue(newFreq >= 0);
		Utils.checkTrue(newResult >= 0);
		Utils.checkTrue(newFreq >= newResult);
		this.execFreqInNew = newFreq;
		this.evalResultInNew = newResult;
	}
	
	public void setMonitoredInfo(int freq, int result) {
		Utils.checkTrue(freq >= 0);
		Utils.checkTrue(result >= 0);
		this.monitoredExec = freq;
		this.monitoredEval = result;
	}
	
	public int getMonitorFreq() {
		return this.monitoredExec;
	}
	
	public int getMonitorEval() {
		return this.monitoredEval;
	}
	
	public boolean isExecutedOnOldVersion() {
		if(!this.isValid()) {
			Utils.fail("should set the execution info first");
		}
		return this.execFreqInOld > 0;
	}
	
	public boolean isExecutedOnNewVersion() {
		if(!this.isValid()) {
			Utils.fail("should set the execution info first");
		}
		return this.execFreqInNew > 0;
	}
	
	//FIXME the following criteria might not be perfect
	public boolean isBehaviorChanged() {
		return this.execFreqInOld == this.execFreqInNew 
		    & this.evalResultInOld == this.evalResultInNew;
	}
	
	private boolean isValid() {
		return this.execFreqInOld >= 0 && this.evalResultInOld >= 0
		    && this.execFreqInNew >= 0 && this.evalResultInNew >= 0;
	}
}