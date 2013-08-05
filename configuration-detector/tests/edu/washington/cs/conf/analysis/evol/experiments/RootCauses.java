package edu.washington.cs.conf.analysis.evol.experiments;

/**
 * Just for debugging uses. Not used in evaluation.
 * */
public class RootCauses {
	
	public static String synopticMethod = "synoptic.main.Main.createInitialPartitionGraph()Lsynoptic/model/PartitionGraph;";
	public static int synopticIndexOld = 233;
	private static String matchedSynopticMethod = synopticMethod; //for checking
	public static int matchedSynopticIndex = 174;
	
	public static String randoopMethod = "randoop.util.ReflectionExecutor.executeReflectionCode(Lrandoop/util/ReflectionCode;Ljava/io/PrintStream;)Ljava/lang/Throwable;";
	public static int randoopIndexOld = 2;
	private static String matchedRandoopMethod = randoopMethod;
	public static int matchedRandoopIndex = 6;
	
	public static String wekaMethod = "weka.core.Instances.stratify(I)V";
	public static int wekaIndexOld = 2;
	private static String matchedWekaMethod = wekaMethod;
	public static int matchedWekaIndex = wekaIndexOld;
	
}
