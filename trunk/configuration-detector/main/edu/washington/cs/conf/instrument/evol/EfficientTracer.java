package edu.washington.cs.conf.instrument.evol;

import java.util.HashMap;
import java.util.Map;

public class EfficientTracer {

	public static String SEP = "##";
	
	//use two large maps to keep track of the evaluation
	//result of each predicate
	
	public static EfficientTracer tracer = getTracer();
	public static EfficientTracer getTracer() {
		if(tracer == null) {
			return new EfficientTracer();
		}
		return tracer;
	}
	private EfficientTracer() {
		//be private on purpose
	}
	
	private Map<String, Integer> predicateFrequency = new HashMap<String, Integer>();
	private Map<String, Integer> predicateResult = new HashMap<String, Integer>();
	
	public void tracePredicateFrequency(String predicateStr) {
		if(!predicateFrequency.containsKey(predicateStr)) {
			predicateFrequency.put(predicateStr, 1);
		} else {
			predicateFrequency.put(predicateStr, predicateFrequency.get(predicateStr));
		}
	}
	
	public void tracePredicateResult(String predicateStr) {
		if(!predicateResult.containsKey(predicateStr)) {
			predicateResult.put(predicateStr, 1);
		} else {
			predicateResult.put(predicateStr, predicateResult.get(predicateStr));
		}
	}
	
}