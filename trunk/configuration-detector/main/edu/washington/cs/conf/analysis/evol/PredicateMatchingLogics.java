package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedList;
import java.util.List;

import plume.Pair;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class PredicateMatchingLogics {
	
	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	
	public final PredicateMatcher matcher;
	
	public PredicateMatchingLogics(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer) {
		Utils.checkNotNull(oldAnalyzer);
		Utils.checkNotNull(newAnalyzer);
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.matcher = new PredicateMatcher(oldAnalyzer.getCallGraph(), newAnalyzer.getCallGraph());
	}
	
	public List<Pair<SSAInstruction, CGNode>> getMatchedPredicates(String methodSig, int index) {
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldAnalyzer.getCallGraph(), methodSig);
		MethodMatchingLogics mmLogics = new MethodMatchingLogics(this.oldAnalyzer, this.newAnalyzer);
		List<CGNode> matchedNewNodes = mmLogics.getMatchedMethods(oldNode);
		
		List<Pair<SSAInstruction, CGNode>> matchedPredicates = new LinkedList<Pair<SSAInstruction, CGNode>>();
		SSAInstruction oldSSA = this.matcher.getPredicateInOldCG(methodSig, index);
		for(CGNode newNode : matchedNewNodes) {
			List<SSAInstruction> ssas = this.matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA);
			List<Pair<SSAInstruction, CGNode>> pairList = new LinkedList<Pair<SSAInstruction, CGNode>>();
			for(SSAInstruction ssa : ssas) {
				pairList.add(new Pair<SSAInstruction, CGNode>(ssa, newNode));
			}
			matchedPredicates.addAll(pairList);
		}
		
		return matchedPredicates;
	}
}