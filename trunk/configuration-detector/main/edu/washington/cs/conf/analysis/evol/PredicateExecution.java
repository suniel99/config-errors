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
	
	public void setExecutionInfo(int oldFreq, int oldResult, int newFreq, int newResult) {
		Utils.checkTrue(oldFreq >= 0);
		Utils.checkTrue(oldResult >= 0);
		Utils.checkTrue(oldFreq >= oldResult);
		Utils.checkTrue(newFreq >= 0);
		Utils.checkTrue(newResult >= 0);
		Utils.checkTrue(newFreq >= newResult);
		this.execFreqInOld = oldFreq;
		this.evalResultInOld = oldResult;
		this.execFreqInNew = newFreq;
		this.evalResultInNew = newResult;
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
	
	private boolean isValid() {
		return this.execFreqInOld >= 0 && this.evalResultInOld >= 0
		    && this.execFreqInNew >= 0 && this.evalResultInNew >= 0;
	}
}