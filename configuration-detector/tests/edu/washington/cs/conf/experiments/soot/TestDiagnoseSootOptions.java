package edu.washington.cs.conf.experiments.soot;

import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.SootExpUtils;
import junit.framework.TestCase;

public class TestDiagnoseSootOptions extends TestCase {
	
	public void test1() {
		String goodRunTrace = "./experiments/soot-database/soot_helloworld_with_keepline.txt";
	    String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
	    List<ConfEntity> sootConfList = SootExpUtils.getSootConfList();
		ConfEntityRepository repo = new ConfEntityRepository(sootConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, false, repo, RankType.SINGLE_IMPORT, 0.2f);
	}
	
}
