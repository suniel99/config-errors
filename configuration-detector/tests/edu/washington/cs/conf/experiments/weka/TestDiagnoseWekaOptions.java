package edu.washington.cs.conf.experiments.weka;

import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import junit.framework.TestCase;

public class TestDiagnoseWekaOptions extends TestCase {
	
	public void test1() {
		String goodRunTrace = "./experiments/weka-database/good-iris.txt";
	    String badRunTrace = "./experiments/weka-database/bad-labor.txt";
	    List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		ConfEntityRepository repo = new ConfEntityRepository(wekaConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.2f);
	}
	
	public void test2() {
		String goodRunTrace = TestComparingWekaTraces.good12;
	    String badRunTrace = "./experiments/weka-database/bad-labor.txt";
	    List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		ConfEntityRepository repo = new ConfEntityRepository(wekaConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.2f);
	}
	
}
