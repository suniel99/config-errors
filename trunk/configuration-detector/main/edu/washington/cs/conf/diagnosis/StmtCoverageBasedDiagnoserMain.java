package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;

public class StmtCoverageBasedDiagnoserMain {

	public static void findResponsibleOptions(Collection<ConfPropOutput> outputs, String[] badStmtFiles, String[] goodStmtFiles) {
		Collection<Collection<StmtExecuted>> goodRuns = new LinkedList<Collection<StmtExecuted>>();
		for(String goodStmtFile : goodStmtFiles) {
			List<StmtExecuted> good = StmtFileReader.readStmts(goodStmtFile);
			goodRuns.add(good);
		}
		
		Collection<Collection<StmtExecuted>> badRuns = new LinkedList<Collection<StmtExecuted>>();
		for(String badStmtFile : badStmtFiles) {
		    List<StmtExecuted> bad = StmtFileReader.readStmts(badStmtFile);
		    badRuns.add(bad);
		}
		
		//compute the coverage differences
		Map<String, Float> scores = TestStmtExecutedDiffer.computeScore(goodRuns, badRuns);
		StmtCoverageBasedDiagnoser diagnoser = new StmtCoverageBasedDiagnoser(outputs, scores);
		
		System.out.println("start to diagnose options: ....");
		
		List<ConfEntity> results = diagnoser.computeResponsibleOptions();
		
		//prune the redundancy
		List<String> entities = new LinkedList<String>();
		for(ConfEntity result : results) {
			if(!entities.contains(result.toString())) {
				entities.add(result.toString());
			}
		}
		
		//print out
		for(int i = 0; i  < entities.size(); i++) {
			System.out.println(i+1 + ". " + entities.get(i));
		}
	}
	
}