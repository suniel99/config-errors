package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisEntity.ProfilePosition;
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
		Collection<List<ConfDiagnosisEntity>> coll = summarizeAllDiagnosisEntity(this.goodRuns,
				this.badRun, this.repository);
		if(type.equals(RankType.TFIDF)) {
			return rankOptionsByTfidf(coll);
		} else if (type.equals(RankType.PURERATIO)) {
			return rankOptionsByRatio(coll);
		} else if (type.equals(RankType.RATIOSUM)) {
			return rankOptionsByRatioSum(coll);
		}else {
			throw new Error("Unrecognized type: " + type);
		}
	}
	
	/**********
	 * All static methods below
	 * *******/
	static Collection<List<ConfDiagnosisEntity>> summarizeAllDiagnosisEntity(
			Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun,
			ConfEntityRepository repository) {
		Collection<List<ConfDiagnosisEntity>> coll = new LinkedList<List<ConfDiagnosisEntity>>();
		
		for(PredicateProfileTuple goodRun : goodRuns) {
			List<ConfDiagnosisEntity> list = summarizeDiagnosisEntity(goodRun, badRun, repository);
			coll.add(list);
		}
		Utils.checkTrue(goodRuns.size() == coll.size());
		return coll;
	}
	
	static List<ConfDiagnosisEntity> summarizeDiagnosisEntity(PredicateProfileTuple goodRun,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		//go through both good profile and bad profile
		for(PredicateProfile goodProfile : goodRun.getAllProfiles()) {
			String uniqueKey = goodProfile.getUniqueKey();
			PredicateProfile badProfile = badRun.lookUpByUniqueKey(uniqueKey);
			ConfDiagnosisEntity result
			    = new ConfDiagnosisEntity(goodProfile.getConfigFullName(), goodProfile.getContext());
			//save attributes
			result.saveScore(ProfilePosition.GOOD_EVAL_COUNT.toString(), goodProfile.getEvaluatingCount());
			result.saveScore(ProfilePosition.GOOD_ENTER_COUNT.toString(), goodProfile.getEnteringCount());
			result.saveScore(ProfilePosition.GOOD_RATIO.toString(), goodProfile.getRatio());
			result.saveScore(ProfilePosition.GOOD_IMPORT.toString(), goodProfile.importanceValue());
			//see the bad profile
			if(badProfile != null) {
				result.saveScore(ProfilePosition.BAD_EVAL_COUNT.toString(), badProfile.getEvaluatingCount());
				result.saveScore(ProfilePosition.BAD_ENTER_COUNT.toString(), badProfile.getEnteringCount());
				result.saveScore(ProfilePosition.BAD_RATIO.toString(), badProfile.getRatio());
				result.saveScore(ProfilePosition.BAD_IMPORT.toString(), badProfile.importanceValue());
			}
			//add to the result
			retList.add(result);
		}
		
		for(PredicateProfile badProfile : badRun.getAllProfiles()) {
			String uniqueKey = badProfile.getUniqueKey();
			if(goodRun.lookUpByUniqueKey(uniqueKey) != null) {
				continue;
			}
			ConfDiagnosisEntity result = new ConfDiagnosisEntity(badProfile.getConfigFullName(), badProfile.getContext());
			result.saveScore(ProfilePosition.BAD_EVAL_COUNT.toString(), badProfile.getEvaluatingCount());
			result.saveScore(ProfilePosition.BAD_ENTER_COUNT.toString(), badProfile.getEnteringCount());
			result.saveScore(ProfilePosition.BAD_RATIO.toString(), badProfile.getRatio());
			result.saveScore(ProfilePosition.BAD_IMPORT.toString(), badProfile.importanceValue());
			//add to the result
			retList.add(result);
		}
		
		//associate with the entity
		for(ConfDiagnosisEntity result : retList) {
			result.setConfEntity(repository);
		}
		
		return retList;
	}
	
	
	//for each list of diagnosis entity, compute a ranked conf-entity, then
	//average the ranking,
	
	/**
	 * Rank by tf-idf
	 * */
	static List<ConfEntity>  rankOptionsByTfidf(Collection<List<ConfDiagnosisEntity>> coll) {
		return null;
	}
	
	/**
	 * Rank by the single highest importance value
	 * */
	static List<ConfEntity> rankOptionsByRatio(Collection<List<ConfDiagnosisEntity>> coll) {
		
		return null;
	}
	
	/**
	 * Rank by the highest importance value summation
	 * */
	static List<ConfEntity> rankOptionsByRatioSum(Collection<List<ConfDiagnosisEntity>> coll) {
		return null;
	}
}
