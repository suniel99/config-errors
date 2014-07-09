package edu.washington.cs.conf.mutation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Globals;

public class ScriptCommand {

	public final String dir;
	public final String script;
	public final List<String> args = new LinkedList<String>();
	
	public ScriptCommand(String dir, String script) {
		this.dir = dir;
		this.script = script;
	}
	
	public void addArgs(Collection<String> args) {
		this.args.addAll(args);
	}
	
	@Override
	public String toString() {
		return "Script: " + dir + Globals.fileSep + script + " " + args;
	}
}
