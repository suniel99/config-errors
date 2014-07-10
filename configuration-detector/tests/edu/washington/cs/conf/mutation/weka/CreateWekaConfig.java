package edu.washington.cs.conf.mutation.weka;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.ConfParser;
import edu.washington.cs.conf.mutation.ExecCommand;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ProgramRunnerByReflection;

public class CreateWekaConfig extends TestCase {

	public static String zeroR_usermanual = "./tests/edu/washington/cs/conf/mutation/weka/zeror_manual.txt"; 
	
	public static String sample_config = "./tests/edu/washington/cs/conf/mutation/weka/zeror_sample_config.txt";
	
	public static String main_zeror = "weka.classifiers.rules.ZeroR";
	
	public void testParseSampleConfig() {
		ConfParser parser = new ConfParser(sample_config);
		parser.parseFile();
		System.out.println(parser.getOptionValueMap());
	}
	
	public void testMutateSampleConfig() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ConfMutator mutator = new ConfMutator(sample_config);
		List<MutatedConf> mutatedConfs = mutator.mutateConfFile();
		
		System.out.println(mutatedConfs.size());
		
		for(MutatedConf mutatedConf : mutatedConfs) {
			System.out.println(mutatedConf.createCmdLine());
			System.out.println("   " + Arrays.asList(mutatedConf.createCmdLineAsArgs()));
			
			String mainClass = main_zeror;
			Class<?> clz = Class.forName(mainClass);
			Method meth = clz.getMethod("main", String[].class);
			String[] args = mutatedConf.createCmdLineAsArgs();
			Object argObj = args;
	        Object o = meth.invoke(null, argObj);
		}
	}
	
	public void testRunZeroRReflectively() {
		ConfMutator mutator = new ConfMutator(sample_config);
		List<MutatedConf> mutatedConfs = mutator.mutateConfFile();
		
		Collection<ExecCommand> cmds = new LinkedList<ExecCommand>();
		cmds.add(new ExecCommand(main_zeror, new String[0]));
		
		ProgramRunnerByReflection runner = new ProgramRunnerByReflection();
		
		runner.setMutatedConfigs(mutatedConfs);
		runner.setCommands(cmds);
		runner.setOutputFile("./weka_output.txt");
		
		runner.execute();
	}
	
}