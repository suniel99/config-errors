package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.util.Utils;
//import junit.framework.TestResult;

public class DefaultExecResultChecker extends ExecResultChecker {

	public DefaultExecResultChecker(Throwable e, String logFile) {
		super(e, logFile);
	}

	@Override
	public boolean pass() {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public String fetchMessage() {
		return null;
	}

}
