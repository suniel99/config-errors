package edu.washington.cs.conf.mutation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

public class TestScriptExecutor extends TestCase {

	public void testJMeterExamplesNoThread() {
		String dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
		List<String> args = Arrays.asList(
				"cmd.exe", "/C",
				"jmeter", "-n", "-t",
				"../threadgroup.jmx", "-l",
				"../output.jtl", "-j", "../testplan.log");
		
		ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithoutThread(args, dir);
		System.out.println(outcome);
	}
	
	public void testJMeterExampleThreaded() {
		String dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
		List<String> args = Arrays.asList(
				"cmd.exe", "/C",
				"jmeter", "-n", "-t",
				"../threadgroup.jmx", "-l",
				"../output.jtl", "-j", "../testplan.log");
		
//		ScriptExecutor.timelimit = 1000;
		ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(args, dir);
		System.out.println(outcome);
	}
	
}
