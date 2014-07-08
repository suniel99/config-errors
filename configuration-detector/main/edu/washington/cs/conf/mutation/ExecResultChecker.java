package edu.washington.cs.conf.mutation;

public abstract class ExecResultChecker {

	//check whether the result is desired or not
	public ExecResultChecker(Throwable e, String logFile) {
		
	}
	
	public abstract boolean pass();
	
	public abstract String fetchMessage();
	
}
