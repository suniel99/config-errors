package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

//the strategy taken to match methods
public class MethodMatchingLogics {
	
	public static boolean USE_FUZZING_MATCHING = true;
	
	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	
	private float threshold = 0.6f;
	private int lookahead = 5;
	
	public final MethodMatcher matcher;
	public final AnalysisScope scope;
	
	public MethodMatchingLogics(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer, AnalysisScope scope) {
		Utils.checkNotNull(oldAnalyzer);
		Utils.checkNotNull(newAnalyzer);
		Utils.checkNotNull(scope);
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.matcher = new MethodMatcher(oldAnalyzer.getCallGraph(), newAnalyzer.getCallGraph(), scope);
		this.scope = scope;
	}
	
	public void setThreshold(float d) {
		Utils.checkTrue(d >= 0f && d <=1f );
		this.threshold = d;
	}
	
	public void setLookahead(int la) {
		Utils.checkTrue(la >= 0);
		this.lookahead = la;
	}
	
	public List<CGNode> getMatchedMethods(String methodSig) {
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(this.oldAnalyzer.getCallGraph(), methodSig);
		Utils.checkNotNull(oldNode);
		return this.getMatchedMethods(oldNode);
	}
	
	public List<CGNode> getMatchedMethods(CGNode oldNode) {
		Utils.checkTrue(this.oldAnalyzer.hasNode(oldNode), "Old node is: " + oldNode.getMethod().getSignature());
		List<CGNode> nodeList = new LinkedList<CGNode>();
		
		CGNode exactMatchedNode = this.matcher.getMethodInNewCG(oldNode.getMethod().getSignature());
		if(exactMatchedNode != null) {
			nodeList.add(exactMatchedNode);
		} else {
			if(USE_FUZZING_MATCHING) {
			    //use the fuzzing matching
			    List<CGNode> fuzzMatchedNodes
			        = this.matcher.getMatchedNodes(oldNode, threshold, lookahead);
			    nodeList.addAll(fuzzMatchedNodes);
			}
		}
		
		return nodeList;
	}
}