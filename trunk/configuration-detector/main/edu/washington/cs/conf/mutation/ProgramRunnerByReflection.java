package edu.washington.cs.conf.mutation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

public class ProgramRunnerByReflection extends ProgramRunner {
	
	public final List<ExecCommand> commands = new LinkedList<ExecCommand>();
	
	public void setCommands(Collection<ExecCommand> cmds) {
		this.commands.addAll(cmds);
	}
	
	@Override
	public void setUpEnv() {
		//TODO may need to call some method to set it
	}

	@Override
	public Collection<ExecResult> execute() {
		Collection<ExecResult> results = new LinkedList<ExecResult>();
		for(ExecCommand cmd : this.commands) {
			String mainClass = cmd.mainMethod;
			String[] mainArgs = cmd.args;
			for(MutatedConf conf : this.mutatedConfigs) {
				
//				System.out.println(cmd + " + " + conf);
				
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
						String[] args = argList.toArray(new String[0]);
						Object argObj = args;
//						System.out.println(argList);
				        meth.invoke(null, argObj);
					} catch (Throwable e) {
						error = e.getCause(); //get the cause of the error
						Utils.checkNotNull(error);
						System.err.println("Error: " + error.getClass() + ", " + error.getMessage());
//						e.printStackTrace();
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