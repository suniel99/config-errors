package edu.washington.cs.conf.analysis.evol.experiments;

/**
 * Just for debugging uses. Not used in evaluation.
 * */
public class RootCauses {
	
	public static String synopticMethod1 = "synoptic.main.Main.createInitialPartitionGraph()Lsynoptic/model/PartitionGraph;";
	public static int synopticIndexOld1 = 89;
	public static int matchedSynopticIndex1 = 72;
	public static String synopticMethod2 = "synoptic.main.Main.exportInitialGraph(Ljava/lang/String;Lsynoptic/model/interfaces/IGraph;)V";
	public static int synopticIndexOld2 = 6;
	public static int matchedSynopticIndex2 = 6;
	
	//=========
	
	public static String randoopMethod = "randoop.util.ReflectionExecutor.executeReflectionCode(Lrandoop/util/ReflectionCode;Ljava/io/PrintStream;)Ljava/lang/Throwable;";
	public static int randoopIndexOld = 2;
	private static String matchedRandoopMethod = randoopMethod;
	public static int matchedRandoopIndex = 6;
	
	public static String wekaMethod = "weka.core.Instances.stratify(I)V";
	public static int wekaIndexOld = 2;
	private static String matchedWekaMethod = wekaMethod;
	public static int matchedWekaIndex = wekaIndexOld;
	
}
