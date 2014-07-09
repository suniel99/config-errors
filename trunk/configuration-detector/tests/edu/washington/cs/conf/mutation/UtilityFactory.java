package edu.washington.cs.conf.mutation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class UtilityFactory {

	public static String TEST_OUTPUT_FILE = "./output-detect/output_file.txt";
	
	public static UserManual createUserManual() {
		UserManual manual = new UserManual();
		manual.addOptionDescription("opt1", "desc1");
		manual.addOptionDescription("opt2", "desc2");
		manual.addOptionDescription("opt3", "desc3");
		manual.addOptionDescription("opt4", "desc4");
		return manual;
	}
	
	public static ProgramRunnerByReflection createRunnerByReflection() {
		ProgramRunnerByReflection runner = new ProgramRunnerByReflection();
		
		//set the details
		runner.setMutatedConfigs(createConfs());
		runner.setOutputFile(TEST_OUTPUT_FILE);
		runner.setCommands(createCmds());
		
		return runner;
	}
	
	public static Collection<MutatedConf> createConfs() {
		Collection<MutatedConf> confs = new LinkedList<MutatedConf>();
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("conf1", "value1");
		map.put("conf2", "value2");
		
		MutatedConf conf1 = new MutatedConf(map, "conf1", "value1-m");
		MutatedConf conf2 = new MutatedConf(map, "conf2", "value2-m");
		
		confs.add(conf1);
		confs.add(conf2);
		
		return confs;
	}
	
	public static Collection<ExecCommand> createCmds() {
		Collection<ExecCommand> coll = new LinkedList<ExecCommand>();
		
		ExecCommand main1 = new ExecCommand(SampleCode.CLASS_NAME, new String[0]);
		ExecCommand main2 = new ExecCommand(SampleCode.CLASS_NAME, new String[]{"bad"});
		
		coll.add(main1);
		coll.add(main2);
		
		return coll;
	}
}
