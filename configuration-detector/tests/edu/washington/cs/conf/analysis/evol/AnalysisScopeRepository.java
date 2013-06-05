package edu.washington.cs.conf.analysis.evol;

public class AnalysisScopeRepository {

	public static AnalysisScope createRandoopScore() {
		AnalysisScope scope = new AnalysisScope();
		scope.setScopePackages(new String[]{"randoop"});
		return scope;
	}
	
}