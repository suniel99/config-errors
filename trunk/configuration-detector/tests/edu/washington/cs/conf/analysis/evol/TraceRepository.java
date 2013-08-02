package edu.washington.cs.conf.analysis.evol;

import edu.washington.cs.conf.instrument.evol.TestInstrumentPrograms;

public class TraceRepository {
	public static String randoopOldTrace = "./evol-experiments/randoop/randoop-1.2.1.txt";
	public static String randoopNewTrace = "./evol-experiments/randoop/randoop-1.3.2.txt";
	
	public static String synopticOldTrace = "./evol-experiments/synoptic/synoptic-0.05.txt";
	public static String synopticNewTrace = "./evol-experiments/synoptic/synoptic-0.1.txt";
	
	public static String jmeterOldTrace = "./evol-experiments/jmeter/jmeter-2.8.txt";
	public static String jmeterNewTrace = "./evol-experiments/jmeter/jmeter-2.9.txt";
	
	public static String wekaOldTrace = "./evol-experiments/weka/weka-3.6.1.txt";
	public static String wekaNewTrace = "./evol-experiments/weka/weka-3.6.2.txt";
	
	public static String jchordP1OldTrace = "./evol-experiments/jchord/problem-1/jchord-2.0.txt";
	public static String jchordP1NewTrace = "./evol-experiments/jchord/problem-1/jchord-2.1.txt";
	public static String jchordP2OldTrace = "./evol-experiments/jchord/problem-2/jchord-2.0.txt";
	public static String jchordP2NewTrace = "./evol-experiments/jchord/problem-2/jchord-2.1.txt";
	
	//execution trace from instrumenting all statements
	static String history_dump = "history_dump.txt";
	static String predicate_dump = "predicate_dump.txt";
	
	public static String synopticOldSig = TestInstrumentPrograms.synoptic_05_sigmap;
	public static String synopticNewSig = TestInstrumentPrograms.synoptic_10_sigmap;
	public static String synopticOldDir = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\tmp-output-folder\\";
	public static String synopticNewDir = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\tmp-output-folder\\";
	public static String synopticOldHistoryDump = synopticOldDir + history_dump;
	public static String synopticOldPredicateDump = synopticOldDir + predicate_dump;
	public static String synopticNewHistoryDump = synopticNewDir + history_dump;
	public static String synopticNewPredicateDump = synopticNewDir + predicate_dump;
	
	public static String jmeterOldSig = TestInstrumentPrograms.jmeter_28_sigmap;
	public static String jmeterNewSig = TestInstrumentPrograms.jmeter_29_sigmap;
	public static String jmeterOldDir = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\bin\\tmp-output-folder\\";
	public static String jmeterNewDir = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\bin\\tmp-output-folder\\";
	public static String jmeterOldHistoryDump = jmeterOldDir + history_dump;
	public static String jmeterOldPredicateDump = jmeterOldDir + predicate_dump;
	public static String jmeterNewHistoryDump = jmeterNewDir + history_dump;
	public static String jmeterNewPredicateDump = jmeterNewDir + predicate_dump;
	
	public static String wekaOldSig = TestInstrumentPrograms.weka_361_sigmap;
	public static String wekaNewSig = TestInstrumentPrograms.weka_362_sigmap;
	public static String wekaDir = "D:\\research\\confevol\\subject-programs\\weka\\tmp-output-folder\\";
	public static String wekaOldHistoryDump = wekaDir + "old_" + history_dump;
	public static String wekaOldPredicateDump = wekaDir + "old_" + predicate_dump;
	public static String wekaNewHistoryDump = wekaDir + "new_" + history_dump;
	public static String wekaNewPredicateDump = wekaDir + "new_" + predicate_dump;
	
	public static String randoopOldSig = TestInstrumentPrograms.randoop_121_sigmap;
	public static String randoopNewSig = TestInstrumentPrograms.randoop_132_sigmap;
	public static String randoopOldDir = "D:\\research\\confevol\\subject-programs\\randoop\\tmp-output-folder\\";
	public static String randoopNewDir = randoopOldDir;
	public static String randoopOldHistoryDump = randoopOldDir + "old_" + history_dump;
	public static String randoopOldPredicateDump = randoopOldDir + "old_" + predicate_dump;
	public static String randoopNewHistoryDump = randoopNewDir + "new_" + history_dump;
	public static String randoopNewPredicateDump = randoopNewDir + "new_" + predicate_dump;
}