package edu.washington.cs.conf.instrument;

import junit.framework.TestCase;

public class TestStackTrace extends TestCase {
	
	public static void main(String[] args) {
		ExecutionContext.pruneNoApp = false;
		new TestStackTrace().testSeeStackTrace();
	}
	
	public void testSeeStackTrace() {
		foo();
	}
	
	void foo() {
		bar();
	}
	
	void bar() {
		ExecutionContext c = ExecutionContext.createContext();
		System.out.println("creating c: " + c);
		System.out.println(c.getApplicationStackTrace());
	}
	
}