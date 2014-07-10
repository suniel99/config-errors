package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.util.Utils;

//encapsulate the execution result
public class ExecResult {

	private boolean pass = false;
	private String message = null;
	private String mutatedConfigOption = null;
	
	private ExecCommand cmd = null; //optional keep the execution context
	private ScriptCommand script = null; //optional keep the execution script
	
	public ExecResult(String message, String mutatedConfigOption, boolean pass) {
		this.message = message;
		this.mutatedConfigOption = mutatedConfigOption;
		this.pass = pass;
	}
	
	public void setCommand(ExecCommand cmd) {
		Utils.checkNotNull(cmd);
		this.cmd = cmd;
	}
	
	public void setScriptCommand(ScriptCommand cmd) {
		Utils.checkNotNull(cmd);
		this.script = cmd;
	}
	
	public ExecCommand getExecCommand() {
		return this.cmd;
	}
	
	public ScriptCommand getScriptCommand() {
		return this.script;
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
