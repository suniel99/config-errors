package edu.washington.cs.conf.analysis.evol;

import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import junit.framework.TestCase;

public class TestPredicateMatcher extends TestCase {

	public void testMatchRandoopPredicate() {
		CodeAnalyzer coder121 = CodeAnalyzerRepository.getRandoop121Analyzer();
		coder121.buildAnalysis();
		
		CodeAnalyzer coder132 = CodeAnalyzerRepository.getRandoop132Analyzer();
		coder132.buildAnalysis();
		
		String methodSig = "randoop.util.ReflectionExecutor.executeReflectionCode(Lrandoop/util/ReflectionCode;Ljava/io/PrintStream;)Ljava/lang/Throwable;";
		int index = 2;
		
		PredicateMatcher matcher = new PredicateMatcher(coder121.getCallGraph(), coder132.getCallGraph());
		CGNode oldNode = matcher.getMethodInOldCG(methodSig);
		System.out.println(oldNode);
		
		CGNode newNode = matcher.getMatchedMethodInNewCG(oldNode);
		System.out.println(newNode);
		
		SSAInstruction oldSsa = matcher.getPredicateInOldCG(methodSig, index);
		System.out.println(oldSsa);
		
		List<SSAInstruction> ssalist = matcher.matchPredicateInNewCG(oldNode, oldSsa);
		System.out.println(ssalist);
		assertEquals("[conditional branch(eq) 7,8]", ssalist.toString());
	}
}
