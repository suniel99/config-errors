package test.slice.examples;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SimpleListExample {

	public static String str1 = null;

	public static String str2 = "";

	public static void main(String[] args) {
		addToList("hello");
	}

	private static List<String> addToList(String input) {
		List<String> list = new ArrayList<String>();
		list.add(str1 + "  ");
		list.add("   ");
		//if (str2 != null) {
			list.add(str2 + "    ");
		//}
//		irrelevantCall(input);
//		usePrintOut();
		return list;
	}
	
	private static void usePrintOut() {
		PrintStream out = System.out;
		out.println(str1);
		out.println("  ");
		out.println(str2);
		List<String> list = new ArrayList<String>();
		list.add("   ");
		if (str2 != null) {
			list.add(str2 + "    ");
		}
	}
	
	private static void irrelevantCall(String input) {
		System.out.println(input + "  ");
	}

}
