package edu.washington.cs.conf.mutation;

import java.io.File;
import java.lang.reflect.Method;

import edu.washington.cs.conf.util.Utils;
//import junit.framework.TestResult;

public class DefaultExecResultChecker extends ExecResultChecker {

	public DefaultExecResultChecker(Throwable e, File logFile) {
		super(e, logFile);
	}
	
	public DefaultExecResultChecker(String message) {
		super(null, message);
	}

	@Override
	public boolean pass() {
		if(super.e != null) {
			return false;
		}
		if(super.oracleCheckingMethod == null) {
			return true;
		}
		return this.executeOralceChecking(oracleCheckingMethod);
	}
	
	//like a script to check the result
	private boolean executeOralceChecking(String methodSig) {
		
		//execute it reflectively
		int lastIndex = methodSig.lastIndexOf(".");
		String className = methodSig.substring(0, lastIndex);
		String methodName = methodSig.substring(lastIndex + 1);
		
		boolean result = false;
		try {
			Class<?> clz = Class.forName(className);
			Method m = clz.getDeclaredMethod(methodName);
			//may need to check the method signature
			Object object = m.invoke(null);
			result = (Boolean)object;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public String fetchMessage() {
		if(super.e != null) {
			return super.e.getMessage();
		}
		if(pass()) {
			return null;
		}
		String message = MessageAnalyzer.fetchErrorMessage(super.logFile);
		return message;
	}

}
