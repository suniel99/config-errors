package edu.washington.cs.conf.instrument;

import instrument.Globals;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.util.Files;

/**
 * An instrumentation class
 * */
public class ConfTracer {
	
	public static int context_length = 0;
	
	public static ConfTracer tracer = new ConfTracer();
	
	private ConfTracer() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	                System.out.println("----------dumping traces to files-------");
	                synchronized(traceMap) {
	                	StringBuilder sb = new StringBuilder();
	                	for(String key : traceMap.keySet()) {
		                	sb.append(key + "#" + traceMap.get(key));
		                	sb.append(Globals.lineSep);
		                }
	                	try {
							Files.writeToFile(sb.toString(), "./trace_dump.txt");
						} catch (IOException e) {
							e.printStackTrace();
						}
	                }
	        }
	    });
	}
	
	List<String> traces = new LinkedList<String>();
	long count = 0;
	
	Map<String, Long> traceMap = new LinkedHashMap<String, Long>();
	
	public void trace(String input) {
		synchronized(traceMap) {
			//the format of line: PRE (or POST), config name, method as context
			//we should record the call context, e.g, stacktrace
			
			String line = input;
			if(context_length > 0) {
			    line = input
			        + "XOXO"
			        + ExecutionContext.getCurrentContextAsString(context_length)
			        ;
			}
			
			if(traceMap.containsKey(line)) {
				traceMap.put(line, traceMap.get(line) + 1);
			} else {
				traceMap.put(line, 1L);
			}
		}
	}
}