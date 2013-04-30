package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.WALAUtils;

import junit.framework.TestCase;

public class TestCodeAnalyzer extends TestCase {

	public void testRandoopIndex() {
		//String randoop121Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		String randoopPath = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\plume.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\jakarta-oro-2.0.8.jar";
		
		String randoopMain = "Lrandoop/main/Main";
		CodeAnalyzer coder = new CodeAnalyzer(randoopPath, randoopMain);
		coder.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder.slicer.setCGType(CG.ZeroCFA);
		
		String methodSig = "randoop.util.ReflectionExecutor.executeReflectionCode(Lrandoop/util/ReflectionCode;Ljava/io/PrintStream;)Ljava/lang/Throwable;";
		int index = 2;
		
		coder.buildAnalysis();
		SSAInstruction ssa = coder.getInstruction(methodSig, index);
		System.out.println(ssa);
		
		String allIRs = coder.getAllInstruction(methodSig);
		System.out.println(allIRs);
		
		//look at the node
		CGNode node = WALAUtils.lookupMatchedCGNode(coder.slicer.getCallGraph(), methodSig);
		
		WALAUtils.printCFG(node);
	}
	
}
