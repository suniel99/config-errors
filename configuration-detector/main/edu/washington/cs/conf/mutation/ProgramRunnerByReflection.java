package edu.washington.cs.conf.mutation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

public class ProgramRunnerByReflection extends ProgramRunner {

	/**
	 * Add some fields to store the commands: main class, and inputs
	 * 
	 * And mutated options
	 * */
	
	public final List<MainClassAndArgs> commands = new LinkedList<MainClassAndArgs>();
	
	public final List<MutatedConf> mutatedConfigs = new LinkedList<MutatedConf>();
	
	private String outputFile = null;
	
	public void setCommands(Collection<MainClassAndArgs> cmds) {
		this.commands.addAll(cmds);
	}
	
	public void setMutatedConfigs(Collection<MutatedConf> configs) {
		this.mutatedConfigs.addAll(configs);
	}
	
	public void setOutputFile(String outputFile) {
		Utils.checkNotNull(outputFile);
		this.outputFile = outputFile;
	}
	
	@Override
	public void setUpEnv() {
		//TODO may need to call some method to set it
	}

	@Override
	public Collection<ExecResult> execute() {
		Collection<ExecResult> results = new LinkedList<ExecResult>();
		for(MainClassAndArgs cmd : this.commands) {
			String mainClass = cmd.mainMethod;
			String[] mainArgs = cmd.args;
			for(MutatedConf conf : this.mutatedConfigs) {
				//create the arg list
				List<String> argList = new LinkedList<String>();
				argList.addAll(Arrays.asList(mainArgs));
				try {
					//add the additional params to the arg list
					argList.add(conf.createCmdLineForMutatedOptions());
					//invoke the main method
					Class<?> clz = Class.forName(mainClass);
					Method meth = clz.getMethod("main", String[].class);
					//register the direct file
					Utils.checkNotNull(this.outputFile);
					FilterPrintStream.register(this.outputFile);
					
					Throwable error = null;
					try {
				        meth.invoke(null, argList.toArray());
					} catch (Throwable e) {
						error = e; //keep the error
					}
				    //unregister it
				    FilterPrintStream.unregister();
				    
				    //create an execution result
				    ExecResult result = ExecResultManager.createExecResult(cmd, conf, error, this.outputFile);
				    results.add(result);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return results;
	}

	@Override
	public void clearEnv() {
		//TODO may need to call some method to clean it
		
	}
}

class MainClassAndArgs {
	public final String mainMethod;
	public final String[] args;
	public MainClassAndArgs(String mainMethod, String[] args) {
		Utils.checkNotNull(mainMethod);
		Utils.checkNoNull(args);
		this.mainMethod = mainMethod;
		this.args = args;
	}
}