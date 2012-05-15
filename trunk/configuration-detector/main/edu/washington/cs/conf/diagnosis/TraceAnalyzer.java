package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.util.Utils;

public class TraceAnalyzer {
	
	public final Collection<String> goodTraces;
	public final Collection<String> badTraces;
	
	public TraceAnalyzer(Collection<String> goodTraces, Collection<String> badTraces) {
		this.goodTraces = goodTraces;
		this.badTraces = badTraces;
	}
	
	public TraceAnalyzer(String goodTraceFileName, String badTraceFileName) {
		TraceFileReader reader = new TraceFileReader(goodTraceFileName, badTraceFileName);
		this.goodTraces = reader.getGoodTraces();
		this.badTraces = reader.getBadTraces();
	}
	
	public Collection<PredicateProfile> getGoodProfiles() {
		return this.createProfiles(this.goodTraces);
	}
	
	public Collection<PredicateProfile> getBadProfiles() {
		return this.createProfiles(this.badTraces);
	}
	
	public Collection<PredicateProfile> createProfiles(Collection<String> traces) {
		Collection<PredicateProfile> profiles = new LinkedHashSet<PredicateProfile>();
		
		//a data structure to memorize the already processed parts
		Map<String, PredicateProfile> profileMap = new LinkedHashMap<String, PredicateProfile>();
		
		for(String trace : traces) {
			String[] splits = this.splitLines(trace);
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
	
	private String[] splitLines(String line) {
		String[] splits = line.split(ConfInstrumenter.SEP);
		Utils.checkTrue(splits.length == 4, "Length: " + splits.length);
		return splits;
	}
}
