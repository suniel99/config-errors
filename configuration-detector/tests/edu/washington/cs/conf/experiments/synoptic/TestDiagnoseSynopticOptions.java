package edu.washington.cs.conf.experiments.synoptic;

import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import junit.framework.TestCase;

public class TestDiagnoseSynopticOptions extends TestCase {
	
	public void test1() {
		String goodRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_bad-injected.txt";
	    String badRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_good.txt";
	    List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
		ConfEntityRepository repo = new ConfEntityRepository(synopticConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.3f);
	}
	
}
