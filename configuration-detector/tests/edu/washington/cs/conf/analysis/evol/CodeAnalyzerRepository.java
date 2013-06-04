package edu.washington.cs.conf.analysis.evol;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Globals;

public class CodeAnalyzerRepository {

	//for the randoop case
	
	public static CodeAnalyzer getRandoop121Analyzer() {
		String randoop121Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		
		String randoopMain = "Lrandoop/main/Main";
		
		CodeAnalyzer coder121 = new CodeAnalyzer(randoop121Path, randoopMain);
		coder121.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder121.slicer.setCGType(CG.ZeroCFA);
		
		return coder121;
	}
	
	public static CodeAnalyzer getRandoop132Analyzer() {
		String randoop132Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\plume.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\jakarta-oro-2.0.8.jar";
		
		String randoopMain = "Lrandoop/main/Main";
		
		CodeAnalyzer coder132 = new CodeAnalyzer(randoop132Path, randoopMain);
		coder132.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder132.slicer.setCGType(CG.ZeroCFA);
		
		return coder132;
	}
	
	
}