package edu.washington.cs.conf.diagnosis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.randoop.TestComparingRandoopGoodBadTraces;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import plume.Option;
import plume.Options;

/**
 * The entry method for ConfDiagnoser
 * */
public class Main {

	private static String VERSION = "ConfDiagnoser, version: 0.1, July 26, 2012";
	
	@Option("Show all help options")
	public static boolean help = false;
	
	@Option("Diagnosing a non-crashing error")
	public static boolean noncrashing = true;
	
	@Option("The distance threshold for comparing two traces")
	public static float distance_threashold = 0.1f;
	
	@Option("The file to contain all diagnosis results")
	public static String diagnose_result_file = "./diagnosis_results.txt";
	
	@Option("The trace file for the bad run")
	public static String bad_run_trace = null;
	
	@Option("A directory containing good run traces. Will do recursive exploration.")
	public static String db_dir = null;
	
	@Option("A file containing configuration options")
	public static String config_options = null;
	
	@Option("The source code dir for the diagnosed program")
	public static String source_dir = null;
	
	@Option("The class path for computing slice")
	public static String classpath_for_slicing = null;
	
	@Option("The main method for computing slicing")
	public static String main_for_slicing = null;
	
	@Option("The CG type used for slicing")
	public static CG cg_type = CG.OneCFA;
	
	@Option("Ingorable classes during slicing computation")
	public static String ingorable_class_file = "JavaAllExclusions.txt";
	
	public static void main(String[] args) throws FileNotFoundException {
		//check the args here
		parse_and_check_args(args);
		//launch the diagnoser
		new Main().nonStaticMain();
	}
	
	private static void parse_and_check_args(String[] args) {
		 Options options = new Options("ConfDiagnoser usage: ", Main.class);
         String[] file_args = options.parse_or_usage(args);
         if(file_args.length != 0) {
             Utils.flushToStd(file_args);
             System.exit(1);
         }
         if(help) {
             Utils.flushToStd(new String[]{VERSION});
             Utils.flushToStd(new String[]{options.usage()});
             System.exit(1);
         }
         List<String> errorMsg = new LinkedList<String>();
         if(bad_run_trace == null) {
             errorMsg.add("You must specify a trace file for the undesired run via: --bad_run_trace");
         }
         if(db_dir == null) {
             errorMsg.add("You must specify a dir containing all good runs via: --db_dir");
         }
         if(config_options == null) {
        	 errorMsg.add("You must specify a file containing all configuration options via: --config_options");
         }
         if(classpath_for_slicing != null && main_for_slicing == null) {
        	 errorMsg.add("After specifying classpath_for_slicing, you must also specify" +
        	 		" the main method via: --main_for_slicing");
         }
         if(classpath_for_slicing == null && main_for_slicing != null) {
        	 errorMsg.add("After specifying main_for_slicing, you must also specify" +
 	 		       " the class path via: --classpath_for_slicing");
         }
         if(!errorMsg.isEmpty()) {
             Utils.flushToStd(errorMsg.toArray(new String[0]));
             Utils.flushToStd(new String[]{options.usage()});
             System.exit(1);
         }
         //set some options
         MainAnalyzer.default_threshold = distance_threashold;
         MainAnalyzer.result_output_file = diagnose_result_file;
	}
	
	//launch the diagnoser
	private void nonStaticMain() throws FileNotFoundException {
		//read the configuration options
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(config_options);
		ConfEntityRepository conf_repo = new ConfEntityRepository(entities);
		
		//get all good runs
		List<File> goodTraceFiles = Files.getFileListing(new File(db_dir));
		String[] good_run_db = new String[goodTraceFiles.size()];
		for(int i = 0; i < goodTraceFiles.size(); i++) {
			good_run_db[i] = goodTraceFiles.get(i).getAbsolutePath();
		}
		
		//decide to perform slicing or not
		Collection<ConfPropOutput> confSlices = null;
		if(classpath_for_slicing != null && main_for_slicing != null) {
			confSlices = CommonUtils.getConfPropOutputs(
					classpath_for_slicing,
					main_for_slicing,
					conf_repo.getConfEntityList(),
					ingorable_class_file,
					cg_type,
					false /**no pruning by default*/);
		}
		
		//start doing diagnosing
		MainAnalyzer.doFiltering = true;
		MainAnalyzer.diagnoseConfigErrors(
				bad_run_trace,
				good_run_db,
				conf_repo,
				source_dir, //source dir 
				confSlices, //conf slice
				null);
	}
}