package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

	@Override
	public Collection<ExecResult> execute() {
		Utils.checkTrue(System.getProperty("os.name").startsWith("Windows"),
				"Only support windows now!");
		
		Collection<ExecResult> results = new LinkedList<ExecResult>();
		for(ScriptCommand cmd : this.commands) {
			String dir = cmd.dir;
			String script = cmd.script;
			for(MutatedConf conf : this.mutatedConfigs) {
				//run the script
				List<String> args = Arrays.asList(new String[]{"cmd.exe", "/C", script});
				ProcessBuilder pb = new ProcessBuilder(args);
				pb.directory(new File(dir));
				pb.redirectErrorStream(true);
				try {
				    Process p = pb.start();
				    final BufferedReader stdInput = new BufferedReader(new InputStreamReader(
							p.getInputStream()), 8 * 1024);
					final BufferedReader stdError = new BufferedReader(new InputStreamReader(
							p.getErrorStream()));
					
					//get the input and output
					BufferReaderThread stdInputThread = new BufferReaderThread(stdInput, "");
					BufferReaderThread stdOutputThread = new BufferReaderThread(stdError, "");
					
					//get the input and output
					stdInputThread.start();
					stdOutputThread.start();
					
					//TODO make the threads join?
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
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
			
			stdInputThread.start();
			stdOutputThread.start();
			

		} catch (IOException e1) {
		} finally  {
			System.out.println("Destroy the process..");
//			p.destroy();
		}

		System.out.println("Done");
	}
}
