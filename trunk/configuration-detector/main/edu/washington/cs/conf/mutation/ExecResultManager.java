package edu.washington.cs.conf.mutation;

/**
 * Observe the execution results of a program, and
 * check it against a testing oracle.
 * */
public class ExecResultManager {
	
	//TODO may need more extensibility
	private static ExecResultChecker oracleChecker = null;
	
	public static ExecResult createExecResult(
			MainClassAndArgs cmd, MutatedConf conf,
			Throwable e, String logFile) {
		
		//how to analyze the log file and create ExecResult object
		oracleChecker = new DefaultExecResultChecker(e, logFile);
		
		boolean pass = oracleChecker.pass();
		String message = oracleChecker.fetchMessage();
		
		return new ExecResult(message, conf.getMutatedConfOption(),pass);
	}
}
