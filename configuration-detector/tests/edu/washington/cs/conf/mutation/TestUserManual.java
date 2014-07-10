package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.mutation.weka.CreateWekaConfig;
import junit.framework.TestCase;

public class TestUserManual extends TestCase {

	public void testWekaZeroRManual() {
		UserManual manual = new UserManual(CreateWekaConfig.zeroR_usermanual);
		for(String key : manual.getAllOptions()) {
			System.out.println(key + "  =>  " + manual.getDescription(key));
		}
	}
	
}
