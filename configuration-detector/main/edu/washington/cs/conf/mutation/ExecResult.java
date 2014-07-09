package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.util.Utils;

//encapsulate the execution result
public class ExecResult {

	private boolean pass = false;
	private String message = null;
	private String mutatedConfigOption = null;
	
	private ExecCommand cmd = null; //optional keep the execution context
	
	public ExecResult(String message, String mutatedConfigOption, boolean pass) {
		this.message = message;
		this.mutatedConfigOption = mutatedConfigOption;
		this.pass = pass;
	}
	
	public void setCommand(ExecCommand cmd) {
		Utils.checkNotNull(cmd);
		this.cmd = cmd;
	}
	
	public ExecCommand getCommand() {
		return this.cmd;
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
	
	@Override
	public String toString() {
		return "mutated: " + this.mutatedConfigOption + ", message: " + message + ", pass: " + pass;
	}
}
