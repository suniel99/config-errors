package edu.washington.cs.conf.nlp;

import edu.washington.cs.conf.mutation.UserManual;
import edu.washington.cs.conf.mutation.weka.CreateWekaConfig;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestIDF extends TestCase {

	
	public void testComputeIDF() {
		UserManual manual = new UserManual(CreateWekaConfig.zeroR_usermanual);
		for(String key : manual.getAllOptions()) {
			System.out.println(key + "  =>  " + manual.getDescription(key));
		}
		TFIDFWeightCalculator cal = new TFIDFWeightCalculator(manual.getAllTextDesc());
		cal.computeTFIDFValues();
		
//		Utils.sortByValue(map, increase)
//		for(String w : cal.getAllWords()) {
//			System.out.println(w + " : " + cal.getIDFValue(w));
//		}
		cal.printIDFValueDescreasingly();
	}
	
}
