package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import plume.Pair;

import edu.washington.cs.conf.util.Utils;

public class PredicateExecInfoAnalyzer {
	
	enum Metrics{Ratio, Behavior}

	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	public final String oldTraceFile;
	public final String newTraceFile;
	
	private Collection<PredicateExecInfo> oldPredicates = null;
	private Collection<PredicateExecInfo> newPredicates = null;
	
	private final PredicateMatchingLogics predicateMatcher;
	
	private Metrics metric = Metrics.Behavior;
	
	public PredicateExecInfoAnalyzer(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer,
			String oldTraceFile, String newTraceFile) {
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.oldTraceFile = oldTraceFile;
		this.newTraceFile = newTraceFile;
		this.predicateMatcher = new PredicateMatchingLogics(oldAnalyzer, newAnalyzer);
	}
	
	public void readPredicates() {
		this.oldPredicates = PredicateExecInfo.createPredicateExecInfoList(oldTraceFile);
		this.newPredicates = PredicateExecInfo.createPredicateExecInfoList(newTraceFile);
	}
	
	public void setMetrics(Metrics m) {
		Utils.checkNotNull(m);
		this.metric = m;
	}

	//check each predicate from old traces with
	public Map<Pair<PredicateExecInfo, PredicateExecInfo>, Float> findBehaviorDeviatedPredicatePairs() {
		if(oldPredicates == null || newPredicates == null) {
			this.readPredicates();
			Utils.checkNotNull(this.oldPredicates);
			Utils.checkNotNull(this.newPredicates);
		}
		//the return maps
		Map<Pair<PredicateExecInfo, PredicateExecInfo>, Float> pairScores
		    = new LinkedHashMap<Pair<PredicateExecInfo, PredicateExecInfo>, Float>();
		
		for(PredicateExecInfo oldPredicate : oldPredicates) {
			//get the matched pairs of a predicate
			List<Pair<SSAInstruction, CGNode>> newPredicatePairList
			    = this.predicateMatcher.getMatchedPredicates(oldPredicate.getMethodSig(), oldPredicate.getIndex());
			//iterate through each matched predicate
			for(Pair<SSAInstruction, CGNode> newPredicatePair : newPredicatePairList) {
				PredicateExecInfo newPredicate =
					PredicateExecInfoFinder.findPredicate(this.newPredicates, newPredicatePair.b, newPredicatePair.a);
				Utils.checkNotNull(newPredicate);
				float deviationScore = 0.0f;
				if(this.metric == Metrics.Behavior) {
					deviationScore = PredicateMetrics.computeBehaviorDiff(oldPredicate, newPredicate);
				} else if (this.metric == Metrics.Ratio) {
					deviationScore = PredicateMetrics.computeTrueRatioDiff(oldPredicate, newPredicate);
				} else {
					throw new Error("Error value: " + this.metric);
				}
				//put to the map
				Pair<PredicateExecInfo, PredicateExecInfo> predicatePair
				    = new Pair<PredicateExecInfo, PredicateExecInfo>(oldPredicate, newPredicate);
				pairScores.put(predicatePair, deviationScore);
			}
		}
		
		return pairScores;
	}
}