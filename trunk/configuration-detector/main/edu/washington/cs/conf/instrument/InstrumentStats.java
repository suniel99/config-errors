package edu.washington.cs.conf.instrument;

public class InstrumentStats {

	static int numOfInsertedInstructions = 0;
	
	public static void addInsertedInstructions(int num) {
		numOfInsertedInstructions += num;
	}
	
	public static void showInstrumentationStats() {
		System.out.println(numOfInsertedInstructions);
	}
}