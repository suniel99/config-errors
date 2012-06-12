package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class TraceAnalyzer {
	
	public final Collection<String> goodTraces;
	public final Collection<String> badTraces;
	
	public TraceAnalyzer(Collection<String> goodTraces, Collection<String> badTraces) {
		this.goodTraces = goodTraces;
		this.badTraces = badTraces;
	}
	
	public TraceAnalyzer(String goodTraceFileName, String badTraceFileName) {
		this.goodTraces = Files.readWholeNoExp(goodTraceFileName);
		this.badTraces = Files.readWholeNoExp(badTraceFileName);
	}
	
	public Collection<PredicateProfile> getGoodProfiles() {
		return createProfiles(this.goodTraces);
	}
	
	public Collection<PredicateProfile> getBadProfiles() {
		return createProfiles(this.badTraces);
	}
	
	public static Collection<PredicateProfile> createProfiles(String traceFileName) {
		Collection<String> traces = Files.readWholeNoExp(traceFileName);
		return createProfiles(traces);
	}
	
	public static PredicateProfileTuple createGoodProfileTuple(String traceFileName, String tupleName) {
		Collection<PredicateProfile> ps = createProfiles(traceFileName);
		return PredicateProfileTuple.createGoodRun(tupleName, ps);
	}
	
	public static PredicateProfileTuple createBadProfileTuple(String traceFileName, String tupleName) {
		Collection<PredicateProfile> ps = createProfiles(traceFileName);
		return PredicateProfileTuple.createBadRun(tupleName, ps);
	}
	
	public static Collection<PredicateProfile> createProfiles(Collection<String> traces) {
		Collection<PredicateProfile> profiles = new LinkedHashSet<PredicateProfile>();
		
		//a data structure to memorize the already processed parts
		Map<String, PredicateProfile> profileMap = new LinkedHashMap<String, PredicateProfile>();
		
		for(String trace : traces) {
			String[] splits = splitLines(trace);
			String confId = splits[1];
			String context = splits[2];
			String point = splits[0];
			int count = Integer.parseInt(splits[3]);
			
			String key = confId + context; //assume this could uniquely identify a position FIXME
			if(!profileMap.containsKey(key)) {
				profileMap.put(key, new PredicateProfile(confId, context));
			}
			PredicateProfile p = profileMap.get(key);
			Utils.checkTrue(p.getEnteringCount() == 0 || p.getEvaluatingCount() == 0);
			if(point.equals(ConfInstrumenter.PRE)) {
				p.setEvaluatingCount(count);
			} else if (point.equals(ConfInstrumenter.POST)) {
				p.setEnteringCount(count);
			} else {
				throw new Error(point);
			}
		}
		
		profiles.addAll(profileMap.values());
		System.err.println("Number of profiles: " + profiles.size());
		
		return profiles;
	}
	
	private static String[] splitLines(String line) {
		String[] splits = line.split(ConfInstrumenter.SEP);
		Utils.checkTrue(splits.length == 4, "Length: " + splits.length);
		return splits;
	}
}
