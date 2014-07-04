package edu.washington.cs.conf.mutation;

import java.io.IOException;

import edu.washington.cs.conf.util.Command;

/**
 * Run a program with the mutated configuration
 * */
public class ProgramRunner {
	
	//run a test script, and or run a test
	//observe its output 

	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("user.dir"));
		String newUserDir = "";
		String command = "javac";
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(command);
	}
	
}