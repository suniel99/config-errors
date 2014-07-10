package edu.washington.cs.conf.mutation;

import java.io.File;

//check if the error message is adaquate
public class MessageAnalyzer {

	//1. check if the error messages contain the text or not
	//2. check if the error messages have consistent meanings with the manuals
	//   - if the error message if far away from similar, then discard
	//   - if multiple manual messages are similar with the error message
	
	public static MessageAdequacy isMessageAdequate(String option, String message, UserManual manual) {
		return new MessageAdequacy();
	}
	
	public static String fetchErrorMessage(File logFile) {
		throw new RuntimeException();
	}
}

class MessageAdequacy {
	
	//TODO analyze the text to decide its adequacy
	public boolean isAdequate() {
		return false;
	}
	
}