package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;

import edu.washington.cs.conf.util.Globals;

public class BufferReaderThread extends Thread {

	public final BufferedReader reader;
	public final String title;
	
	public final StringBuilder sb = new StringBuilder();
	
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
			    sb.append(s);
			    sb.append(Globals.lineSep);
		    }
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getMessage() {
		return this.sb.toString();
	}
}
