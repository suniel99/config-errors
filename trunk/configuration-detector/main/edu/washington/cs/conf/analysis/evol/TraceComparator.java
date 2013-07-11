package edu.washington.cs.conf.analysis.evol;

import java.util.List;
import java.util.Set;

import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;

/**
 * Given two execution traces, find the predicate
 * that have different behaviors
 * */
public class TraceComparator {

	public final ExecutionTrace oldTrace;
	public final ExecutionTrace newTrace;
	
	private CodeAnalyzer oldCoder = null;
	private CodeAnalyzer newCoder = null;
	
	private AnalysisScope scope = null;
	private AnalysisCache cache = null;
	
	public TraceComparator(ExecutionTrace oldTrace, ExecutionTrace newTrace) {
		Utils.checkNotNull(oldTrace);
		Utils.checkNotNull(newTrace);
		this.oldTrace = oldTrace;
		this.newTrace = newTrace;
	}
	
	public Set<PredicateExecution> getPredicateOnlyExecutedInOldVersion() {
		return null;
	}
	
	public Set<PredicateExecution> getPredicateOnlyExecutedInNewVersion() {
		return null;
	}
	
	public Set<PredicateExecution> getPredicateExecutedInBothVersions() {
		return null;
	}
	
	public Set<PredicateExecution> getPredicateWithDifferentBehaviors() {
		return null;
	}
	

	/****************
	 * Simple setters
	 ***************/
	public void setOldCoder(CodeAnalyzer oldCoder) {
		this.oldCoder = oldCoder;
	}

	public void setNewCoder(CodeAnalyzer newCoder) {
		this.newCoder = newCoder;
	}

	public void setScope(AnalysisScope scope) {
		this.scope = scope;
	}

	public void setCache(AnalysisCache cache) {
		this.cache = cache;
	}
	
}
