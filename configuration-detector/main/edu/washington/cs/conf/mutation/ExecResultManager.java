package edu.washington.cs.conf.mutation;

import java.io.File;

/**
 * Observe the execution results of a program, and
 * check it against a testing oracle.
 * */
public class ExecResultManager {
	
	//TODO may need more extensibility
	private static ExecResultChecker oracleChecker = null;
	
	public static ExecResult createReflectionExecResult(
			ExecCommand cmd, MutatedConf conf,
			Throwable e, String logFilePath) {
		
		//how to analyze the log file and create ExecResult object
		oracleChecker = new DefaultExecResultChecker(e, new File(logFilePath));
		
		boolean pass = oracleChecker.pass();
		String message = oracleChecker.fetchMessage();
		
		//create the exec result object
		ExecResult result = new ExecResult(message, conf.getMutatedConfOption(), pass);
		
		return result;
	}
	
	public static ExecResult createScriptExecResult(
			ScriptCommand cmd, MutatedConf conf, String inputMessage) {
		
		oracleChecker = new DefaultExecResultChecker(inputMessage);
		
		boolean pass = oracleChecker.pass();
		String message = oracleChecker.fetchMessage();
		
		//create the exec result object
		ExecResult result = new ExecResult(message, conf.getMutatedConfOption(), pass);
		
		return result;
	}
}
