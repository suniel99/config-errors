package edu.washington.cs.conf.instrument.evol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import edu.washington.cs.conf.util.Files;

public class EfficientTracer {

	public static String SEP = "##";
	public static String PRED_SEP = "==";
	public static String EVAL_SEP = ":";
	
	public static String EXEC = "EXEC:";
	public static String EVAL = "EVAL:";
	
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
		Runtime.getRuntime().addShutdownHook(this.createShutdownThread());
	}
	
	private Map<String, Integer> predicateFrequency = new HashMap<String, Integer>();
	private Map<String, Integer> predicateResult = new HashMap<String, Integer>();
	private List<String> predicateExecHistory = new LinkedList<String>();
	
	public void tracePredicateFrequency(String predicateStr) {
		predicateExecHistory.add(EXEC + predicateStr); //the full history
		if(!predicateFrequency.containsKey(predicateStr)) {
			predicateFrequency.put(predicateStr, 1);
		} else {
			predicateFrequency.put(predicateStr, predicateFrequency.get(predicateStr) + 1);
		}
//		System.out.println("freq: " + predicateStr);
	}
	
	public void tracePredicateResult(String predicateStr) {
		predicateExecHistory.add(EVAL + predicateStr); //the full history
		if(!predicateResult.containsKey(predicateStr)) {
			predicateResult.put(predicateStr, 1);
		} else {
			predicateResult.put(predicateStr, predicateResult.get(predicateStr) + 1);
		}
//		System.out.println("result: " + predicateStr);
	}
	
	private Thread createShutdownThread() {
		final String lineSep = System.getProperty("line.separator");
		return new  Thread() {
	        @Override
	        public void run() {
	        	if(predicateFrequency.isEmpty()) {
	        		System.out.println("----------no traces recorded-------");
	        		return;
	        	}
	        	System.out.println("----------dumping traces to files-------");
	            synchronized(predicateFrequency) {
	            	synchronized(predicateResult) {
	            		//record the evaluation results
	            		StringBuilder sb = new StringBuilder();
	                	for(String key : predicateFrequency.keySet()) {
	                    	sb.append(key + PRED_SEP + predicateFrequency.get(key)
	                    			+ EVAL_SEP + (predicateResult.containsKey(key) ? predicateResult.get(key) : 0));
	                    	sb.append(lineSep);
	                    }
	                	//record the full history
	                	StringBuilder historySb = new StringBuilder();
	                	for(String line : predicateExecHistory) {
	                		historySb.append(line);
	                		historySb.append(lineSep);
	                	}
	                	try {
	                		long time = System.currentTimeMillis();
	                		String fileName = "./tmp-output-folder/predicate_dump_" + time + ".txt";
	                		File f = new File(fileName);
	                		
	                		if(!f.getParentFile().exists()) {
	                			System.out.println("Create folder: " + f.getParentFile().getAbsolutePath());
	                			f.getParentFile().mkdirs();
	                		}
	                		System.out.println("write to file: " + f.getAbsolutePath());
	                		//Did not re-use Files utility since this class need to be separated
	                		//and packed into one jar
	        				EfficientTracer.writeToFile(sb.toString(), f, false);
	        				

	                		String historyFileName = "./tmp-output-folder/history_dump_" + time + ".txt";
	                		File hf = new File(historyFileName);
	                		System.out.println("write to file: " + hf.getAbsolutePath());
	                		EfficientTracer.writeToFile(historySb.toString(), hf, false);
	                		
	        			} catch (IOException e) {
	        				e.printStackTrace();
	        			}
	            	}
	            }
	        }
		};
		
	}
	
	private static void writeToFile(String s, File file, Boolean append) throws IOException {
	    BufferedWriter writer= new BufferedWriter(new FileWriter(file, append));
	    try{
	      writer.append(s);
	    } finally {
	      writer.close();
	    }        
   }
}