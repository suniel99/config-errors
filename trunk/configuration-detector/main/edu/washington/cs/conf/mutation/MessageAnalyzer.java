package edu.washington.cs.conf.mutation;

//check if the error message is adaquate
public class MessageAnalyzer {

	//1. check if the error messages contain the text or not
	//2. check if the error messages have consistent meanings with the manuals
	//   - if the error message if far away from similar, then discard
	//   - if multiple manual messages are similar with the error message
	
	public static MessageAdequacy isMessageAdequate(String option, String message, UserManual manual) {
		return new MessageAdequacy();
	}
	
	public static String fetchErrorMessage(String logFile) {
		throw new RuntimeException();
	}
}

class MessageAdequacy {
	
	public boolean isAdequate() {
		return true;
	}
	
}