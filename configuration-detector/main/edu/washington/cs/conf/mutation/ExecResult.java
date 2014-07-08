package edu.washington.cs.conf.mutation;

//encapsulate the execution result
public class ExecResult {

	private boolean pass = false;
	private String message = null;
	private String mutatedConfigOption = null;
	
	public ExecResult(String message, String mutatedConfigOption, boolean pass) {
		this.message = message;
		this.mutatedConfigOption = mutatedConfigOption;
		this.pass = pass;
	}
	
	public boolean pass() {
		return pass;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getMutatedOption() {
		return this.mutatedConfigOption;
	}
}
