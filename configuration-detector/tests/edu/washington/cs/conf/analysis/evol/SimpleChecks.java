package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.HashSet;
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
}
