package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class ProgramRunnerByScript extends ProgramRunner {

    public final List<ScriptCommand> commands = new LinkedList<ScriptCommand>();
	
	public void setCommands(Collection<ScriptCommand> cmds) {
		this.commands.addAll(cmds);
	}
	
	@Override
	public void setUpEnv() {
		// TODO do that later

	}

	/**
	 * String dir = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609";
	 * String script = "startjetty.bat";
	 * 
	 * http://stackoverflow.com/questions/14981435/how-do-i-stop-jetty
	 * */
	@Override
	public Collection<ExecResult> execute() {
		Utils.checkTrue(System.getProperty("os.name").startsWith("Windows"),
				"Only support windows now!");
		
		Collection<ExecResult> results = new LinkedList<ExecResult>();
		for(ScriptCommand cmd : this.commands) {
			String dir = cmd.dir;
			String script = cmd.script;
			for(MutatedConf conf : this.mutatedConfigs) {
				this.setupConfigEnv(conf);
				
				//run the script
				List<String> args = Arrays.asList(new String[]{"cmd.exe", "/C", script});
				ProcessBuilder pb = new ProcessBuilder(args);
				pb.directory(new File(dir));
				pb.redirectErrorStream(true);
				try {
				    Process p = pb.start();
				    final BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
							p.getInputStream()), 8 * 1024);
					final BufferedReader stdError = new BufferedReader(new InputStreamReader(
							p.getErrorStream()));
					
					//get the input and output
					BufferReaderThread stdOutputThread = new BufferReaderThread(stdOutput, "");
					BufferReaderThread stdErrorThread = new BufferReaderThread(stdError, "");
					
					//get the input and output
					stdOutputThread.start();
					stdErrorThread.start();
					
					//TODO make the threads join?
//					Thread.sleep(2000);
//					p.destroy();
//					stdInputThread.interrupt();
//					stdOutputThread.interrupt();
					
					String outputMsg = stdOutputThread.getMessage();
					String errorMsg = stdErrorThread.getMessage();
					String message = outputMsg + Globals.lineSep + errorMsg;
					
					ExecResult result = ExecResultManager.createScriptExecResult(cmd, conf, message);
				    results.add(result);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
				
				this.revertConfigEnv(conf);
			}
		}
		return results;
	}
	
	private void setupConfigEnv(MutatedConf conf) {
		
	}
	
	private void revertConfigEnv(MutatedConf conf) {
		
	}

	@Override
	public void clearEnv() {
		// TODO do that later

	}

	// test launching commands
	public static void main(String[] args) throws IOException {

//		Process p = null;
		try {
			
			//Process proc = rt.exec("cmd /c start cmd.exe /K \"cd " + locaction);
			
			String dir = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609";
			String script = "startjetty.bat";
			
//			ProcessBuilder pb = new ProcessBuilder(script);
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList(new String[]
                {"cmd.exe", "/C", script}));
			pb.directory(new File(dir));
			pb.redirectErrorStream(true);
			final Process p = pb.start();
			
			
			//encapsulate the below in a thread
			
			
			
			// Process p =
			// Runtime.getRuntime().exec("cmd /C dir E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\");
//			Process p = Runtime.getRuntime().exec("cmd /C " + dir + script);
			final BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()), 8 * 1024);
			final BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			
			BufferReaderThread stdInputThread = new BufferReaderThread(stdInput, "Standard input");
			BufferReaderThread stdOutputThread = new BufferReaderThread(stdError, "Standard output");
			

			stdInputThread.setDaemon(true);
			stdOutputThread.setDaemon(true);
			
//			stdInputThread.start();
//			stdOutputThread.start();
			
			Thread.sleep(2000);
			p.destroy();
//			stdInputThread.stop();
//			stdOutputThread.stop();
			
//			stdInputThread.join(2000);
//			stdOutputThread.join(2000);
			
			
			System.out.println(stdInputThread.getMessage());

		} catch (IOException e1) {
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally  {
			System.out.println("Destroy the process..");
//			p.destroy();
		}

		System.out.println("Done");
	}
}
