package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.util.Utils;

public abstract class ExecResultChecker {

	protected Throwable e;
	protected String logFile;
	protected String oracleCheckingMethod;
	
	//check whether the result is desired or not
	public ExecResultChecker(Throwable e, String logFile) {
		this.e = e;
		this.logFile = logFile;
	}
	
	public void setOracleCheckingMethod(String method) {
		Utils.checkNotNull(method);
		this.oracleCheckingMethod = method;
	}
	
	public abstract boolean pass();
	
	public abstract String fetchMessage();
	
}
