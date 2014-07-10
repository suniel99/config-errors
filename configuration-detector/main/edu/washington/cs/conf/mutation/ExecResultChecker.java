package edu.washington.cs.conf.mutation;

import java.io.File;

import edu.washington.cs.conf.util.Utils;

public abstract class ExecResultChecker {

	protected Throwable e;
	protected File logFile;
	protected String oracleCheckingMethod;
	protected String message;
	
	//check whether the result is desired or not
	public ExecResultChecker(Throwable e, File logFile) {
		this.e = e;
		this.logFile = logFile;
	}
	
	//e can be null
	public ExecResultChecker(Throwable e, String message) {
		this.e = e;
		this.message = message;
	}
	
	public void setOracleCheckingMethod(String method) {
		Utils.checkNotNull(method);
		this.oracleCheckingMethod = method;
	}
	
	public abstract boolean pass();
	
	public abstract String fetchMessage();
	
}
