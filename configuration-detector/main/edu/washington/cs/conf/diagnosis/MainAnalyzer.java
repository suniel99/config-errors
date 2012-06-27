package edu.washington.cs.conf.diagnosis;

import instrument.Globals;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.CrossRunRank;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.util.Utils;

/**
 * The main entry of the predicate profile based diagnosis.
 * Input: 
 *     - a number of good runs (files)
 *     - a bad run (files)
 *     - a threashold of selecting similar runs
 *     - a selection methodology
 *     - a ranking methodology
 *     - a cross run ranking methodology
 * Output:
 *     - a list of ranked configuration options
 * */
public class MainAnalyzer {
	
	public static boolean doFiltering = false;
	public static boolean amortizeNoise = false;
	public static int thresholdcount = 3;
	
	private float distanceThreshold = 0.3f; //distance
	private DistanceType distanceType = DistanceType.INTERPRODUCT;
	private RankType rankType = RankType.SINGLE_IMPORT;
	private CrossRunRank crossRank = CrossRunRank.HIGHEST_RANK_AVG;
	private final ConfEntityRepository repository;
	
	private String sourceDir = null;
	private Collection<ConfPropOutput> outputs = null;

	public final PredicateProfileTuple badRun;
	private final List<PredicateProfileTuple> goodRuns
	    = new LinkedList<PredicateProfileTuple>();
	public final PredicateProfileDatabase goodRunDb;
	
	public MainAnalyzer(PredicateProfileTuple badRun, Collection<PredicateProfileTuple> goodRuns,
			ConfEntityRepository repository) {
		Utils.checkNotNull(badRun);
		Utils.checkNotNull(goodRuns);
		Utils.checkTrue(goodRuns.size() > 0);
		this.badRun = badRun;
		this.goodRuns.addAll(goodRuns);
		this.goodRunDb = new PredicateProfileDatabase(this.goodRuns);
		this.repository = repository;
	}
	
	public MainAnalyzer(String badRunTraceFile, Collection<String> goodRunTraceFiles,
			ConfEntityRepository repository) {
		this(badRunTraceFile, goodRunTraceFiles, repository, null, null);
	}
	
	public MainAnalyzer(String badRunTraceFile, Collection<String> goodRunTraceFiles,
			ConfEntityRepository repository, String srcDir, Collection<ConfPropOutput> propOutputs) {
		Utils.checkNotNull(badRunTraceFile);
		Utils.checkNotNull(goodRunTraceFiles);
		Utils.checkTrue(goodRunTraceFiles.size() > 0);
		
		//set the dir
		this.sourceDir = srcDir;
		this.outputs = propOutputs;
		
		//create the bad run
		Collection<PredicateProfile> badProfiles = TraceAnalyzer.createProfiles(badRunTraceFile);
		this.setSrcLineAndText(badProfiles);
		this.badRun = PredicateProfileTuple.createBadRun("badrun", badProfiles);
		//create the good runs
		int index = 0;
		for(String goodRunTraceFile : goodRunTraceFiles) {
			Collection<PredicateProfile> goodProfiles = TraceAnalyzer.createProfiles(goodRunTraceFile);
			this.setSrcLineAndText(goodProfiles);
			PredicateProfileTuple goodProfile = PredicateProfileTuple.createGoodRun("goodrun-" + (index++), goodProfiles);
			this.goodRuns.add(goodProfile);
		}
		this.goodRunDb = new PredicateProfileDatabase(this.goodRuns);
		this.repository = repository;
	}
	
	private void setSrcLineAndText(Collection<PredicateProfile> profiles) {
		if(this.sourceDir != null && this.outputs != null) {
			ConfUtils.setUpLineNumberAndSource(this.sourceDir, this.outputs, profiles);
		}
	}
	
	public List<ConfDiagnosisOutput> computeResponsibleOptions() {
		this.showParameters(); //for debugging purpose
		List<PredicateProfileTuple> similarProfiles
		   = selectSimilarProfileTuples(this.goodRunDb, this.badRun, this.distanceType, this.distanceThreshold);
		System.err.println("Number of similar profiles: " + similarProfiles.size());
		PredicateProfileBasedDiagnoser diagnoser = createDiagnoser(similarProfiles, this.badRun, this.repository);
		List<ConfDiagnosisOutput> rankedOutput = diagnoser.computeResponsibleOptions(this.rankType);
		return rankedOutput;
	}
	
	private void showParameters() {
		StringBuilder sb = new StringBuilder();
		sb.append("Basic info:");
		sb.append(Globals.lineSep);
		sb.append("  number of good runs: " + this.goodRunDb.getAllTuples().size());
		sb.append("Selecting similar traces: ");
		sb.append(Globals.lineSep);
		sb.append("  distance type: " + this.distanceType);
		sb.append(Globals.lineSep);
		sb.append("  threshold: " + this.distanceThreshold);
		sb.append(Globals.lineSep);
		sb.append("For configuration option ranking: ");
		sb.append(Globals.lineSep);
		sb.append("  rank type: " + this.rankType);
		sb.append(Globals.lineSep);
		sb.append("  cross run ranking: " + this.crossRank);
		sb.append(Globals.lineSep);
		System.err.println(sb.toString());
	}
	
	static PredicateProfileBasedDiagnoser createDiagnoser(Collection<PredicateProfileTuple> similarProfiles,
			PredicateProfileTuple target, ConfEntityRepository repo) {
		PredicateProfileBasedDiagnoser diagnoser = new PredicateProfileBasedDiagnoser(similarProfiles, target, repo);
		return diagnoser;
	}
	
	static List<PredicateProfileTuple> selectSimilarProfileTuples(PredicateProfileDatabase db, PredicateProfileTuple target,
			DistanceType distanceType, Float threshold) {
		List<PredicateProfileTuple> similarTuples = db.findSimilarTuples(target, distanceType, threshold);
		return similarTuples;
	}

	public float getThreshold() {
		return distanceThreshold;
	}

	public void setThreshold(float threshold) {
		this.distanceThreshold = threshold;
	}

	public DistanceType getDistanceType() {
		return distanceType;
	}

	public void setDistanceType(DistanceType distanceType) {
		this.distanceType = distanceType;
	}

	public RankType getRankType() {
		return rankType;
	}

	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

	public CrossRunRank getCrossRank() {
		return crossRank;
	}

	public void setCrossRank(CrossRunRank crossRank) {
		this.crossRank = crossRank;
	}

	public PredicateProfileTuple getBadRun() {
		return badRun;
	}

	public List<PredicateProfileTuple> getGoodRuns() {
		return goodRuns;
	}
	
	public ConfEntityRepository getConfEntityRepository() {
		return this.repository;
	}
	

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public Collection<ConfPropOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(Collection<ConfPropOutput> outputs) {
		this.outputs = outputs;
	}
}