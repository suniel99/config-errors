package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.util.Utils;
import junit.framework.TestResult;

public class JUnitExecResultChecker extends ExecResultChecker {
	
	public final TestResult result;
	
	public JUnitExecResultChecker(TestResult result) {
		Utils.checkNotNull(result);
		this.result = result;
	}

	@Override
	public boolean pass() {
		return result.wasSuccessful();
	}

}
