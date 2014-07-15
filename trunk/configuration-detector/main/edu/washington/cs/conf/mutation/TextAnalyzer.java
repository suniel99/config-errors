package edu.washington.cs.conf.mutation;

import java.util.Collection;

public class TextAnalyzer {

	//check if the error message contains the key, which can
	//be a configuration option name or value
	//this is an initial implementation
	public static boolean containsText(String text, String key) {
		return text.indexOf(key) != -1;
	}
	
	public boolean closeEnoughToText(String text, String key) {
		return false;
	}
	
	public boolean isClosestToAllText(String targetText, Collection<String> allText, String key) {
		return false;
	}
	
}
