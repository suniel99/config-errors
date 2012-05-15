package edu.washington.cs.conf.diagnosis;

import java.util.Collection;

import edu.washington.cs.conf.util.Files;

public class TraceFileReader {
	
	public final String goodTrace;
	
	public final String badTrace;
	
	public TraceFileReader(String goodTrace, String badTrace) {
		this.goodTrace = goodTrace;
		this.badTrace = badTrace;
	}
	
	public Collection<String> getGoodTraces() {
		return Files.readWholeNoExp(goodTrace);
	}
	
	public Collection<String> getBadTraces() {
		return Files.readWholeNoExp(badTrace);
	}
}