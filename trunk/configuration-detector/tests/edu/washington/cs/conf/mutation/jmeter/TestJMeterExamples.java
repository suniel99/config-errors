package edu.washington.cs.conf.mutation.jmeter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ProgramRunnerByScript;
import edu.washington.cs.conf.mutation.ScriptCommand;
import junit.framework.TestCase;

//by running from command line
public class TestJMeterExamples extends TestCase {

	//dir: E:\conf-vul\programs\jmeter\apache-jmeter-2.9\bin
	//command: jmeter -n -t ../threadgroup.jmx -l ../output.jtl -j ../testplan.log
	String dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
	String script = "jmeter";
	
	public void testRunSimpleExample() {
		Collection<ScriptCommand> cmds = new LinkedList<ScriptCommand>();
		//create a command
		Collection<String> args = Arrays.asList("-n", "-t", "../threadgroup.jmx", "-l",
				"../output.jtl", "-j", "../testplan.log");
//		args = Arrays.asList();
//		script = "dir";
		
		ScriptCommand cmd = new ScriptCommand(dir, script);
		cmd.addArgs(args);
		cmds.add(cmd);
		
		//set the configs
		Collection<MutatedConf> configs = new LinkedList<MutatedConf>();
		Map<String, String> options = new LinkedHashMap<String, String>();
		Set<String> onOffs = new LinkedHashSet<String>();
		MutatedConf conf = new MutatedConf(options, onOffs, "", "");
		configs.add(conf);
		
		//create a runner
		ProgramRunnerByScript runner = new ProgramRunnerByScript();
		runner.setCommands(cmds);
		runner.setMutatedConfigs(configs);
		
		runner.execute();
	}
	
}