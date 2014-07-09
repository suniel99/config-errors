package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;

public class BufferReaderThread extends Thread {

	public final BufferedReader reader;
	public final String title;
	
	public BufferReaderThread(BufferedReader p, String title) {
		super();
		this.reader = p;
		this.title = title;
	}
	
	@Override
	public void run() {
		String s = null;
		System.out.println(title);
		try {
		    while ((s = this.reader.readLine()) != null) {
			    System.out.println(s);
		    }
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
