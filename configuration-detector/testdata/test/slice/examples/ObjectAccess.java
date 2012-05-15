package test.slice.examples;

public class ObjectAccess {
	
	public static String v = "hello";
	
	public static void main(String[] args) {
		ObjectWrapper w1 = new ObjectWrapper();
		v = "world";
		w1.access(v);
		w1.doSomething();
		ObjectWrapper w2 = new ObjectWrapper();
		//w2.access("hello");
		w2.doSomething();
		w1.doSomething();
	}
	
}

class ObjectWrapper {
	Object obj;
	
	public void access(String str) {
		obj = str;
	}
	
	public void doSomething() {
		System.out.println(obj);
	}
}