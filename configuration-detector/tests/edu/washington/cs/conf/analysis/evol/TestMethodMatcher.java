package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;

import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestMethodMatcher extends TestCase {

	public void testMethodRandoopCode() {
		CodeAnalyzer coder121 = CodeAnalyzerRepository.getRandoop121Analyzer();
		coder121.buildAnalysis();
		
		CodeAnalyzer coder132 = CodeAnalyzerRepository.getRandoop132Analyzer();
		coder132.buildAnalysis();
		
		AnalysisScope scope = AnalysisScopeRepository.createRandoopScore();
		
		String methodSig = "randoop.util.ReflectionExecutor.executeReflectionCode(Lrandoop/util/ReflectionCode;Ljava/io/PrintStream;)Ljava/lang/Throwable;";
		
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(coder121.getCallGraph(), methodSig);
		CGNode newNode = WALAUtils.lookupMatchedCGNode(coder132.getCallGraph(), methodSig);
		
		System.out.println("old node: " + oldNode);
		System.out.println("new node: " + newNode);
		
		boolean matched = false;
		
		MethodMatcher.debug = false;
		MethodMatcher matcher = new MethodMatcher(coder121.getCallGraph(),
				coder132.getCallGraph(), scope);
		
		List<CGNode> matchedNodes = new LinkedList<CGNode>();
		for(CGNode n : coder132.getCallGraph()) {
			if(WALAUtils.getFullMethodName(n.getMethod()).startsWith("randoop.")) {
				matched = matcher.matchNodes(oldNode, n, 0.6f, 5);
				if(matched) {
					matchedNodes.add(n);
				}
			}
		}
		assertEquals(1, matchedNodes.size());
		assertEquals("Node: < Application, Lrandoop/util/ReflectionExecutor, " +
				"executeReflectionCode(Lrandoop/util/ReflectionCode;" +
				"Ljava/io/PrintStream;)Ljava/lang/Throwable; > Context: " +
				"Everywhere",
				matchedNodes.get(0).toString());
	}
}
