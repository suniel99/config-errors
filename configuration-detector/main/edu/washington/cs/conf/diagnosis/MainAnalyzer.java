package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
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
	
	private float threshold = 0.8f;
	private DistanceType distanceType = DistanceType.EUCLIDEAN;
	private RankType rankType = RankType.TFIDF_IMPORT;
	private CrossRunRank crossRank = CrossRunRank.HIGHEST_RANK_AVG;
	private final ConfEntityRepository repository;
	
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
		Utils.checkNotNull(badRunTraceFile);
		Utils.checkNotNull(goodRunTraceFiles);
		Utils.checkTrue(goodRunTraceFiles.size() > 0);
		//create the bad run
		Collection<PredicateProfile> badProfiles = TraceAnalyzer.createProfiles(badRunTraceFile);
		this.badRun = PredicateProfileTuple.createBadRun("badrun", badProfiles);
		//create the good runs
		int index = 0;
		for(String goodRunTraceFile : goodRunTraceFiles) {
			Collection<PredicateProfile> goodProfiles = TraceAnalyzer.createProfiles(goodRunTraceFile);
			PredicateProfileTuple goodProfile = PredicateProfileTuple.createGoodRun("goodrun-" + (index++), goodProfiles);
			this.goodRuns.add(goodProfile);
		}
		this.goodRunDb = new PredicateProfileDatabase(this.goodRuns);
		this.repository = repository;
	}
	
	public List<ConfDiagnosisOutput> computeResponsibleOptions() {
		List<PredicateProfileTuple> similarProfiles
		   = selectSimilarProfileTuples(this.goodRunDb, this.badRun, this.distanceType, this.threshold);
		PredicateProfileBasedDiagnoser diagnoser = createDiagnoser(similarProfiles, this.badRun, this.repository);
		List<ConfDiagnosisOutput> rankedOutput = diagnoser.computeResponsibleOptions(this.rankType);
		return rankedOutput;
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
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
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
}