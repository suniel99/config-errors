package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.util.Utils;

public class PredicateProfileBasedDiagnoser {
	
	public enum RankType {TFIDF, PURERATIO, RATIOSUM};

	public final Collection<PredicateProfileTuple> goodRuns;
	
	public final PredicateProfileTuple badRun;
	
	public final ConfEntityRepository repository;
	
	public PredicateProfileBasedDiagnoser(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		Utils.checkNotNull(goodRuns);
		Utils.checkNotNull(badRun);
		Utils.checkNotNull(repository);
		this.goodRuns = goodRuns;
		this.badRun = badRun;
		this.repository = repository;
	}
	
	public List<ConfEntity> computeResponsibleOptions(RankType type) {
		if(type.equals(RankType.TFIDF)) {
			return rankOptionsByTfidf(this.goodRuns, this.badRun);
		} else if (type.equals(RankType.PURERATIO)) {
			return rankOptionsByRatio(this.goodRuns, this.badRun);
		} else if (type.equals(RankType.RATIOSUM)) {
			return rankOptionsByRatioSum(this.goodRuns, this.badRun);
		}else {
			throw new Error("Unrecognized type: " + type);
		}
	}
	
	static List<ConfEntity>  rankOptionsByTfidf(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun) {
		
		return null;
	}
	
	/**
	 * Rank by the single highest importance value
	 * */
	static List<ConfEntity> rankOptionsByRatio(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun) {
		
		return null;
	}
	static List<ConfEntity> rankOptionsByRatio(PredicateProfileTuple goodRun,
			PredicateProfileTuple badRun) {
		Map<ConfEntity, Float> entities = new HashMap<ConfEntity, Float>();
		return null;
	}
	
	/**
	 * Rank by the highest importance value summation
	 * */
	static List<ConfEntity> rankOptionsByRatioSum(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun) {
		return null;
	}
}
