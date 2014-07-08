package edu.washington.cs.conf.mutation;

public class TextMatcher {

	//check if the error message contains the key, which can
	//be a configuration option name or value
	//this is an initial implementation
	public static boolean matchText(String text, String key) {
		return text.indexOf(key) != -1;
	}
	
}
