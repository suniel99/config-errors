package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * The simplest way to find matched predicate, by name,
 * then by the location
 * */
public class SimplePredicateMatcher {
	

	public final CallGraph oldGraph;
	public final CallGraph newGraph;
	public final AnalysisScope scope;
	public final AnalysisCache analysisCache;
	
	public SimplePredicateMatcher(CallGraph oldGraph, CallGraph newGraph,
			AnalysisScope scope, AnalysisCache cache) {
		Utils.checkNotNull(oldGraph);
		Utils.checkNotNull(newGraph);
		Utils.checkNotNull(scope);
		Utils.checkNotNull(cache);
		this.oldGraph = oldGraph;
		this.newGraph = newGraph;
		this.scope = scope;
		this.analysisCache = cache;
	}
	
	//get the corresponding predicate
	public PredicateBehaviorAcrossVersions getMatchedPredicateInNewVersion(PredicateBehaviorAcrossVersions oldPredicate) {
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(this.oldGraph, oldPredicate.methodSig);
		Utils.checkNotNull(oldNode);
		SSAInstruction oldSSA = WALAUtils.getInstruction(oldNode, oldPredicate.index);
		//find the corresponding new node
		CGNode newNode = WALAUtils.lookupMatchedCGNode(this.newGraph, oldPredicate.methodSig);
		if(newNode == null) {
			return null; //the same name node does not exist
		}
		//if exist, then find the predicate matching the given ones
		//check if the CG node has been changed or not?
		if(this.areIdenticalNodes(oldNode, newNode)) {
			//return the same index
			return new PredicateBehaviorAcrossVersions(oldPredicate.methodSig, oldPredicate.index);
		}
		//FIXME did not consider other matched info yet
		//re-use the fine-grained matcher?
		
		return null;
	}
	
	public PredicateBehaviorAcrossVersions getMatchedPredicateInOldVersion(PredicateBehaviorAcrossVersions newPredicate) {
		CGNode newNode = WALAUtils.lookupMatchedCGNode(this.newGraph, newPredicate.methodSig);
		Utils.checkNotNull(newNode);
		SSAInstruction newSSA = WALAUtils.getInstruction(newNode, newPredicate.index);
		//get the corresponding old node
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(this.oldGraph, newPredicate.methodSig);
		if(oldNode == null) {
			return null;
		}
		//check if two nodes are identical
		if(this.areIdenticalNodes(newNode, oldNode)) {
			return new PredicateBehaviorAcrossVersions(newPredicate.methodSig, newPredicate.index);
		}
		
		//FIXME did not consider other matched info yet
		return null;
	}
	
	private boolean areIdenticalNodes(CGNode newNode, CGNode oldNode) {
		SSAInstruction[] newSSAs = newNode.getIR().getInstructions();
		SSAInstruction[] oldSSAs = oldNode.getIR().getInstructions();
		if(newSSAs.length != oldSSAs.length) {
			return false;
		}
		for(int i = 0; i < newSSAs.length; i++) {
			SSAInstruction newSSA = newSSAs[i];
			SSAInstruction oldSSA = oldSSAs[i];
			if(newSSA == null && oldSSA != null) {
				return false;
			}
			if(oldSSA == null && newSSA != null) {
				return false;
			}
			if(oldSSA == null && newSSA == null) {
				continue;
			}
			boolean same =  newSSA.getClass().equals(oldSSA.getClass()); //FIXME approximate
			if(!same) {
				return false;
			}
		}
		return true;
	}
}
