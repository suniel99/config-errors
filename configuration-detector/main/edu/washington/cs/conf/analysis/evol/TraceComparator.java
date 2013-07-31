package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedHashSet;
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
	
	private SimplePredicateMatcher matcher = null;
	
	public TraceComparator(ExecutionTrace oldTrace, ExecutionTrace newTrace) {
		Utils.checkNotNull(oldTrace);
		Utils.checkNotNull(newTrace);
		this.oldTrace = oldTrace;
		this.newTrace = newTrace;
		this.matcher = new SimplePredicateMatcher(oldCoder.getCallGraph(), newCoder.getCallGraph(),
				scope, cache);
	}
	
	public Set<PredicateExecution> getPredicateOnlyExecutedInOldVersion() {
		Set<PredicateExecution> oldPredicates = this.getOldExecutedPredicates();
		Set<PredicateExecution> newPredicates = this.getNewExecutedPredicates();
		
        //create the old execution entities from the execution traces
		Set<PredicateExecution> oldExecutions = new LinkedHashSet<PredicateExecution>();
		for(PredicateExecution oldPred : oldPredicates) {
			//the matched predicate in the new program version
			PredicateExecution matchedPred = this.getMatchedPredicateInNewVersion(oldPred);
			//the predicate executed in the new version
			PredicateExecution newPred = this.getIncludePredicate(matchedPred, newPredicates);
			//if there is no match, or the matched predicate is not executed
			if(matchedPred == null || newPred == null) {
				PredicateExecution exec = new PredicateExecution(oldPred.methodSig, oldPred.index);
				exec.setOldExecutionInfo(oldPred.getMonitorFreq(), oldPred.getMonitorEval());
				oldExecutions.add(exec);
			}
		}
		
		return oldExecutions;
	}
	
	public Set<PredicateExecution> getPredicateOnlyExecutedInNewVersion() {
		Set<PredicateExecution> oldPredicates = this.getOldExecutedPredicates();
		Set<PredicateExecution> newPredicates = this.getNewExecutedPredicates();
		
		Set<PredicateExecution> newExecutions = new LinkedHashSet<PredicateExecution>();
		
		for(PredicateExecution newPred : newPredicates) {
			PredicateExecution matchedPred = this.getMatchedPredicateInOldVersion(newPred);
			PredicateExecution oldPred = this.getIncludePredicate(matchedPred, oldPredicates);
			//if the predicate is not matched, or the matched in not executed
			if(matchedPred == null || oldPred == null) {
				PredicateExecution exec = new PredicateExecution(newPred.methodSig, newPred.index);
				exec.setNewExecutionInfo(newPred.getMonitorFreq(), newPred.getMonitorEval());
				newExecutions.add(exec);
			}
		}
		
		return newExecutions;
	}
	
	public Set<PredicateExecution> getPredicateExecutedInBothVersions() {
		Set<PredicateExecution> oldPredicates = this.getOldExecutedPredicates();
		Set<PredicateExecution> newPredicates = this.getNewExecutedPredicates();
		
		Set<PredicateExecution> bothExecutions = new LinkedHashSet<PredicateExecution>();
		
		for(PredicateExecution oldPred : oldPredicates) {
			PredicateExecution matchedPred = this.getMatchedPredicateInNewVersion(oldPred);
			PredicateExecution newPred = this.getIncludePredicate(matchedPred, newPredicates);
			//there is a matched predicate, and that predicate has been executed
			if(matchedPred != null || newPred != null) {
				//here, use the new predicate's method signature and its index num
				PredicateExecution exec = new PredicateExecution(newPred.methodSig, newPred.index);
				exec.setOldExecutionInfo(oldPred.getMonitorFreq(), oldPred.getMonitorEval());
				exec.setNewExecutionInfo(newPred.getMonitorFreq(), newPred.getMonitorEval());
				bothExecutions.add(exec);
			}
		}
		
		return bothExecutions;
	}
	
	//this is a subset of the above predicates executed in both versions
	public Set<PredicateExecution> getPredicateWithDifferentBehaviors() {
		Set<PredicateExecution> diffPreds = new LinkedHashSet<PredicateExecution>();
		for(PredicateExecution predExec : this.getPredicateExecutedInBothVersions()) {
			if(predExec.isBehaviorChanged()) {
				diffPreds.add(predExec);
			}
		}
		return diffPreds;
	}
	
	private PredicateExecution getMatchedPredicateInOldVersion(PredicateExecution newPredExec) {
		//call the predicate matching logic
		return this.matcher.getMatchedPredicateInOldVersion(newPredExec);
	}
	
	private PredicateExecution getMatchedPredicateInNewVersion(PredicateExecution oldPredExec) {
		//this method should call the predicate matching logic
		return this.matcher.getMatchedPredicateInNewVersion(oldPredExec);
	}
	
	//find a predicate execution in the set having the same signature...
	private PredicateExecution getIncludePredicate(PredicateExecution pred, Set<PredicateExecution> set) {
		for(PredicateExecution exec : set) {
			if(pred.methodSig.equals(exec.methodSig) && pred.index == exec.index) {
				return exec;
			}
		}
		return null;
	}
	
	//the cached results
	private Set<PredicateExecution> oldPredicates = null;
	private Set<PredicateExecution> getOldExecutedPredicates() {
		if(oldPredicates == null) {
			oldPredicates = this.oldTrace.getExecutedPredicates();
		}
		return oldPredicates;
	}
	private Set<PredicateExecution> newPredicates = null;
	private Set<PredicateExecution> getNewExecutedPredicates() {
		if(newPredicates == null) {
			newPredicates = this.newTrace.getExecutedPredicates();
		}
		return newPredicates;
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
