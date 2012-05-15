package test.slice.examples;

import java.util.ArrayList;
import java.util.List;

public class SharingStringExample {

	public static String str1 = "hello";
	
	public static String str2 = "world";
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add(str1 + "again");
		list.add(str2 + " I come.");
	}
	
}
