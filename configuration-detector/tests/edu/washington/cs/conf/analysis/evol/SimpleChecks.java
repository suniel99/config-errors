package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.util.Utils;

//just for experiment purpose
public class SimpleChecks {

	public static Set<String> getUnmatchedOldMethods(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet) {
		Set<String> oldMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldMethods.add(execInfo.context);
		}
		Set<String> newMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newMethods.add(execInfo.context);
		}
		return Utils.minus(oldMethods, newMethods);
	}

	public static Set<String> getUnmatchedNewMethods(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet) {
		Set<String> oldMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldMethods.add(execInfo.context);
		}
		Set<String> newMethods = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newMethods.add(execInfo.context);
		}
		return Utils.minus(newMethods, oldMethods);
	}
	
	public static Set<String> getUnmatchedOldPredicates(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet
	    ) {
		Set<String> oldPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldPredicates.add(execInfo.getPredicateSig());
		}
		Set<String> newPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newPredicates.add(execInfo.getPredicateSig());
		}
		return Utils.minus(oldPredicates, newPredicates);
	}
	
	public static Set<String> getUnmatchedNewPredicates(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet
	    ) {
		Set<String> oldPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : oldSet) {
			oldPredicates.add(execInfo.getPredicateSig());
		}
		Set<String> newPredicates = new HashSet<String>();
		for(PredicateExecInfo execInfo : newSet) {
			newPredicates.add(execInfo.getPredicateSig());
		}
		return Utils.minus(newPredicates, oldPredicates);
	}
	
	//get behaviorally changed predicates
	public static Set<PredicateBehaviorAcrossVersions> getMatchedPredicateExecutions(
			Collection<PredicateExecInfo> oldSet,
			Collection<PredicateExecInfo> newSet
			) {
		
		Map<String, PredicateExecInfo> oldPredMap = new LinkedHashMap<String, PredicateExecInfo>();
		for(PredicateExecInfo oldPred : oldSet) {
			oldPredMap.put(oldPred.getPredicateSig(), oldPred);
		}
		
		Map<String, PredicateExecInfo> newPredMap = new LinkedHashMap<String, PredicateExecInfo>();
		for(PredicateExecInfo newPred : newSet) {
			newPredMap.put(newPred.getPredicateSig(), newPred);
		}
		
		Set<PredicateBehaviorAcrossVersions> predSet = new HashSet<PredicateBehaviorAcrossVersions>();
		for(String oldPredSig : oldPredMap.keySet()) {
			if(newPredMap.containsKey(oldPredSig)) {
				PredicateExecInfo oldExecInfo = oldPredMap.get(oldPredSig);
				PredicateExecInfo newExecInfo = newPredMap.get(oldPredSig);
				//create a predicate execution object
				PredicateBehaviorAcrossVersions execObj = new PredicateBehaviorAcrossVersions(oldExecInfo.getMethodSig(), oldExecInfo.getIndex());
				execObj.setOldExecutionInfo(oldExecInfo.evalFreqCount, oldExecInfo.evalResultCount);
				execObj.setNewExecutionInfo(newExecInfo.evalFreqCount, newExecInfo.evalResultCount);
				predSet.add(execObj);
			}
		}
		
		return predSet;
	}
}
