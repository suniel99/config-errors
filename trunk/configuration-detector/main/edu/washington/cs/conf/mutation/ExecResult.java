package edu.washington.cs.conf.mutation;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.washington.cs.conf.util.Utils;

enum Status {Pass, Fail, Hang, Init}

//encapsulate the execution result
public class ExecResult {

	private Status status = Status.Init;
	private String message = null;
	private String mutatedConfigOption = null;
	private String mutatedValue = null;
	
	private ExecCommand cmd = null; //optional keep the execution context
	private ScriptCommand script = null; //optional keep the execution script
	
	//keep track of all used configs
	private Map<String, String> usedConfigs = null;
	
	public ExecResult(String message, String mutatedConfigOption,
			String mutatedValue, Status status) {
		Utils.checkTrue(status != Status.Init);
		this.message = message;
		this.mutatedConfigOption = mutatedConfigOption;
		this.mutatedValue = mutatedValue;
		this.status = status;
	}
	
	public void setCommand(ExecCommand cmd) {
		Utils.checkNotNull(cmd);
		this.cmd = cmd;
	}
	
	public void setScriptCommand(ScriptCommand cmd) {
		Utils.checkNotNull(cmd);
		this.script = cmd;
	}
	
	public void setUsedConfigs(Map<String, String> configMap) {
		Utils.checkNotNull(configMap);
		this.usedConfigs = new LinkedHashMap<String, String>();
		this.usedConfigs.putAll(configMap);
	}
	
	public ExecCommand getExecCommand() {
		return this.cmd;
	}
	
	public ScriptCommand getScriptCommand() {
		return this.script;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public boolean pass() {
		return this.status == Status.Pass;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getMutatedOption() {
		return this.mutatedConfigOption;
	}
	
	public String getMutatedValue() {
		return this.mutatedValue;
	}
	
	public String dumpCmdWithConfigs() {
		StringBuilder sb = new StringBuilder();
		if(this.cmd != null) {
			sb.append(this.cmd.toString());
		} else if (this.script != null) {
			throw new RuntimeException("Not implemented yet.");
		}
		if(usedConfigs != null) {
		    sb.append(this.usedConfigs.toString());
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "mutated: " + this.mutatedConfigOption + ", with value: " + this.mutatedValue +  
		    ", message: " + message + ", status: " + this.status;
	}
}
