package edu.washington.cs.conf.mutation;

public class SampleCode {
	
	public static String CLASS_NAME = "edu.washington.cs.conf.mutation.SampleCode";

	public static void good() {
		System.out.println("this is good!");
	}
	
	public static void bad() {
		System.err.println("so bad!");
		throw new RuntimeException("so bad!");
	}
	
	
	public static void main(String[] args) {
		if(args.length == 0) {
			good();
		} else {
			if(args[0].equals("bad")) {
			    bad();
			} else {
				good();
			}
		}
	}
	
}