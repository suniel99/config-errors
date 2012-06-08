package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TestStmtExecutedDiffer extends TestCase {
	
	public void testDiffer() {
		List<StmtExecuted> good1 = StmtFileReader.readStmts("./tests/edu/washington/cs/conf/diagnosis/stmts_dump_good1.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts("./tests/edu/washington/cs/conf/diagnosis/stmts_dump_good2.txt");
		List<StmtExecuted> bad1 = StmtFileReader.readStmts("./tests/edu/washington/cs/conf/diagnosis/stmts_dump_bad.txt");
		
		StmtExecuted.addSourceNumber("./subjects/testdata.jar", good1,good2, bad1);
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		StmtExecutedDiffer differ = new StmtExecutedDiffer(goodRuns, badRuns);
		differ.computeStmtScores();
		Map<String, Float> scores = differ.getRankedStmts();
		
		for(String s : scores.keySet()) {
			System.out.println(s + "   " + scores.get(s));
		}
		
	}

}
