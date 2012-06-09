package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
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
			return rankOptionsByTfidf(this.goodRuns, this.badRun, this.repository);
		} else if (type.equals(RankType.PURERATIO)) {
			return rankOptionsByRatio(this.goodRuns, this.badRun, this.repository);
		} else if (type.equals(RankType.RATIOSUM)) {
			return rankOptionsByRatioSum(this.goodRuns, this.badRun, this.repository);
		}else {
			throw new Error("Unrecognized type: " + type);
		}
	}
	
	static List<ConfEntity>  rankOptionsByTfidf(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		//use tf-idf
		return null;
	}
	
	/**
	 * Rank by the single highest importance value
	 * */
	static List<ConfEntity> rankOptionsByRatio(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		
		return null;
	}
	//FIXME, probably not correct
	static List<ConfEntity> rankOptionsByRatio(PredicateProfileTuple goodRun,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		Map<ConfEntity, Float> entities = new HashMap<ConfEntity, Float>();
		
		//compare a good run with a bad run to see which config deviates most
		for(PredicateProfile p : goodRun.getAllProfiles()) {
			ConfEntity config = repository.lookupConfEntity(p.getConfigFullName());
			Utils.checkNotNull(config, "Null? : " + config.getFullConfName());
			PredicateProfile pBad = badRun.lookUpByUniqueKey(p.getUniqueKey());
			if(pBad == null) {
				
			} else {
				
			}
		}
		
		//sort the entity and then return all its key
		List<ConfEntity> rankedConfigs = Utils.sortByValueAndReturnKeys(entities, false);
		return rankedConfigs;
	}
	
	/**
	 * Rank by the highest importance value summation
	 * */
	static List<ConfEntity> rankOptionsByRatioSum(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		return null;
	}
}
