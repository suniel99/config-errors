package edu.washington.cs.conf.instrument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;

import edu.washington.cs.conf.util.Utils;

public abstract class AbstractInstrumenter {

	  protected final boolean disasm = false;
	  protected final boolean verify = true;
	  protected OfflineInstrumenter instrumenter;
	  
	  public static String PRE = "evaluating";
	  public static String POST = "entering";
	  public static String SEP = "#";
	  public static String CONF_SEP = "=-="; //separate multiple confs to reduce instrumentatiion num
	  
	  //this is for separating context with other relevant information
	  //such as line number, source text, etc.
	  //see TraceAnalyzer for usage example
	  public static String SUB_SEP = "%%"; //not used for context separation
	  public static String INDEX_SEP = "_index_";
	  
	  public void instrument(String inputElement, String outputJar) throws Exception {
		  System.out.println("start instrumentating");
	      instrumenter = new OfflineInstrumenter();
	      Writer w = new BufferedWriter(new FileWriter("report", false));
	      instrumenter.addInputElement(inputElement);
	      instrumenter.setOutputJar(new File(outputJar));
	      instrumenter.setPassUnmodifiedClasses(true);
	      instrumenter.beginTraversal();
	      ClassInstrumenter ci;
	      //do the instrumentation
	      while ((ci = instrumenter.nextClass()) != null) {
	    	  try {
	             doClass(ci, w);
	    	  } catch (Throwable e) {
	    		  e.printStackTrace();
	    		  continue;
	    	  }
	      }
	      instrumenter.close();
	  }
	  
	  protected abstract void doClass(final ClassInstrumenter ci, Writer w) throws Exception;
	  
	  //some utility methods
	  protected String getMethodSignature(MethodData d) {
		  String sig = d.getSignature();
		  String name = d.getName();
		  String jvmClassName = d.getClassType();
		  Utils.checkTrue(jvmClassName.startsWith("L"));
		  Utils.checkTrue(jvmClassName.endsWith(";"));
		  String javaClassName =  Utils.translateSlashToDot(jvmClassName.substring(1, jvmClassName.length() - 1));
		  return javaClassName + "." + name + sig;
	  }
}
