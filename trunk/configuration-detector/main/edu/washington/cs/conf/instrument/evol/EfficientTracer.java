package edu.washington.cs.conf.instrument.evol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import edu.washington.cs.conf.util.Files;
/**
 * Note that this class should not use classes not defined in
 * the standard JDK, since it would be used separately.
 * */

public class EfficientTracer {

	public static String SEP = "##";
	public static String PRED_SEP = "==";
	public static String EVAL_SEP = ":";
	
	public static String EXEC = "EXEC:";
	public static String EVAL = "EVAL:";
	public static String NORMAL = "NOR:";
	
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
	
	public void traceNormalInstruction(String str) {
		predicateExecHistory.add(NORMAL + str);
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
	                	
	                	try {
	                		String dir = "./tmp-output-folder";
	                		File dirFile = new File(dir);
	                		if(!dirFile.exists()) {
	                			System.out.println("Create folder: " + dirFile.getAbsolutePath());
	                			dirFile.mkdirs();
	                		} else {
	                			if(dirFile.isFile()) {
	                				System.err.println("Exit, a file: " + dirFile.getAbsolutePath() + " exist!");
	                				System.exit(1);
	                			}
	                		}
	                		
	                		long time = System.currentTimeMillis();
	                		String predicateFileName = dir + "/predicate_dump_" + time + ".txt";
	                		File predicateFile = new File(predicateFileName);
	                		System.out.println("write predicate behaviors to file: " + predicateFile.getAbsolutePath());
	        				EfficientTracer.writeToFile(sb.toString(), predicateFile, false);
	        				
	        				//due to the large volume the history must be written directly
	                		String historyFileName = "./tmp-output-folder/history_dump_" + time + ".txt";
	                		File hf = new File(historyFileName);
	                		System.out.println("write full history to file: " + hf.getAbsolutePath());
	                		EfficientTracer.directWriteToFile(predicateExecHistory, hf);
	                		
	        			} catch (IOException e) {
	        				e.printStackTrace();
	        			}
	            	}
	            }
	        }
		};
		
	}

	//Did not re-use Files utility since this class need to be separated
	//and packed into one jar
	
	private static void writeToFile(String s, File file, Boolean append) throws IOException {
	    BufferedWriter writer= new BufferedWriter(new FileWriter(file, append));
	    try{
	      writer.append(s);
	    } finally {
	      writer.close();
	    }        
   }

   private static final String lineSep = System.getProperty("line.separator");
	
   private static <T> void directWriteToFile(Collection<T> coll, File file) throws IOException {
	   if(file.exists()) {
		   file.delete();
		   System.err.println("Delete existing file: " + file.getAbsolutePath());
	   }
	   BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));  //use append here
	   try {
	       for(T t : coll) {
		       writer.append(t + "");
		       writer.append(lineSep);
	       }
	   } finally {
		   writer.close();
	   }
   }
}