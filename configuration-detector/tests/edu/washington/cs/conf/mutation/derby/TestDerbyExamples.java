package edu.washington.cs.conf.mutation.derby;

import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ScriptExecOutcome;
import edu.washington.cs.conf.mutation.ScriptExecutor;
import junit.framework.TestCase;

public class TestDerbyExamples extends TestCase {

	public void testLaunch_1() {
		String dir = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor";
		String confFilePath = dir + "\\derby.properties";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		
		ScriptExecutor.name = "Derby";
		
		for(MutatedConf mutate : mutates) {
			mutate.writeToFile(confFilePath);
			
			//java -jar ..\lib\derbyrun.jar ij connection.txt
			List<String> args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "ij",
					"connection-query.txt");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
					args, dir);
			System.out.println(outcome);

			args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "ij",
			        "connection-remove.txt");
			outcome = ScriptExecutor.executeScriptWithThread(args, dir);
			System.out.println(outcome);
		}
		
	}
	
}
