package edu.washington.cs.conf.mutation;

import java.io.File;

import edu.washington.cs.conf.util.Utils;

//check if the error message is adaquate
public class MessageAnalyzer {

	//1. check if the error messages contain the text or not
	//2. check if the error messages have consistent meanings with the manuals
	//   - if the error message if far away from similar, then discard
	//   - if multiple manual messages are similar with the error message
	
	public static MessageAdequacy isMessageAdequate(ExecResult result, UserManual manual) {
		String mutatedConfigOption = result.getMutatedOption();
		String mutatedValue = result.getMutatedValue();
		String errorMsg = result.getMessage();
		
		Utils.checkNotNull(errorMsg);
		
		MessageAdequacy msgAdequacy = new MessageAdequacy();
		
		boolean containOptionName = TextAnalyzer.containsText(errorMsg, mutatedConfigOption);
		boolean containOptionValue = TextAnalyzer.containsText(errorMsg, mutatedValue);
		
		
		return msgAdequacy;
	}

}

class MessageAdequacy {
	
	private boolean containOptionName = false;
	private boolean containOptionValue = false;
	private boolean closeEnoughToUserManual = false;
	private boolean closestToAllUserDesc = false;
	
	//give some explanations of why this is sufficient
	
	//TODO analyze the text to decide its adequacy
	//more info, in particular, if the message is not enough, we need
	//to show why it is not enough, and which one should be improved
	public boolean isAdequate() {
		return containOptionName || containOptionValue
		    || closeEnoughToUserManual || closestToAllUserDesc;
	}
	
	public void setContainOptionName(boolean flag) {
		this.containOptionName = flag;
	}
	
	public void setContainOptionValue(boolean flag) {
		this.containOptionValue = flag;
	}
	
	public void setCloseEnoughToUserManual(boolean flag) {
		this.closeEnoughToUserManual = flag;
	}
	
	public void setClosestToAllUserDesc(boolean flag) {
		this.closeEnoughToUserManual = flag;
	}
	
	public String getExplanation() {
		if(!isAdequate()) {
			return "Not contain option values/names, nor not close enough to user description";
		} else {
			return "Adequate!";
		}
	}
	
	public String toString() {
		return "Adequate: contain name:" + containOptionName + ", contain value: "
		    + containOptionValue + ", close to manual: "
		    + closeEnoughToUserManual + ", close to all user descs: " + closestToAllUserDesc;
	}
}