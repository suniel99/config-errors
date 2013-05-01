package edu.washington.cs.conf.analysis.evol;

import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Globals;
import junit.framework.TestCase;

public class TestPredicateMatcher extends TestCase {

	public void testMatchRandoopPredicate() {
		String randoop121Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		String randoop132Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\plume.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\jakarta-oro-2.0.8.jar";
		
		String randoopMain = "Lrandoop/main/Main";
		
		CodeAnalyzer coder121 = new CodeAnalyzer(randoop121Path, randoopMain);
		coder121.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder121.slicer.setCGType(CG.ZeroCFA);
		coder121.buildAnalysis();
		
		CodeAnalyzer coder132 = new CodeAnalyzer(randoop132Path, randoopMain);
		coder132.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder132.slicer.setCGType(CG.ZeroCFA);
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
