package edu.washington.cs.conf.analysis.evol;

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
		
		System.out.println(oldNode);
		System.out.println(newNode);
		
//		System.out.println("----------------");
//		WALAUtils.printCFG(oldNode);
//		System.out.println("----------------");
//		WALAUtils.printCFG(newNode);
		
		boolean matched = false;
		
		MethodMatcher.debug = false;
		MethodMatcher matcher = new MethodMatcher(coder121.getCallGraph(),
				coder132.getCallGraph(), scope);
		
//		matched = matcher.matchNodes(oldNode, newNode, 0.6f, 5);
//		System.out.println(matched);
//		
//		MethodMatcher.debug = true;
//		methodSig = "randoop.main.Help.handle([Ljava/lang/String;)Z";
//		newNode = WALAUtils.lookupMatchedCGNode(coder132.getCallGraph(), methodSig);
//		System.out.println("----------------");
//		WALAUtils.printCFG(newNode);
//		matched = matcher.matchNodes(oldNode, newNode, 0.6f, 5);
//		System.out.println(matched);
		
//		MethodMatcher.debug = true;
//		methodSig = "randoop.util.ProgressDisplay.display()V";
//		newNode = WALAUtils.lookupMatchedCGNode(coder132.getCallGraph(), methodSig);
//		System.out.println("----------------");
//		WALAUtils.printCFG(oldNode);
//		System.out.println("----------------");
//		WALAUtils.printCFG(newNode);
//		matched = matcher.matchNodes(oldNode, newNode, 0.6f, 5);
//		System.out.println(matched);
		
		for(CGNode n : coder132.getCallGraph()) {
			if(WALAUtils.getFullMethodName(n.getMethod()).startsWith("randoop.")) {
				matched = matcher.matchNodes(oldNode, n, 0.6f, 5);
				if(matched) {
					System.out.println(matched + " -- " + n);
				}
			}
		}
	}
	
	
}
