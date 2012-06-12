package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisEntity.RawDataType;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisEntity.ScoreType;
import edu.washington.cs.conf.util.Utils;

/**
 * Given a set of bad runs and one good run, find out which
 * configuration options should be responsible for.
 * */
public class PredicateProfileBasedDiagnoser {
	
	public enum RankType {TFIDF_RATIO, TFIDF_IMPORT, SINGLE_RATIO, SINGLE_IMPORT, RATIO_SUM, IMPORT_SUM};
	
	public enum CrossRunRank {HIGHEST_RANK_AVG, SCORE_SUM_RANK};

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
	
	public List<ConfDiagnosisOutput> computeResponsibleOptions(RankType type) {
		Collection<List<ConfDiagnosisEntity>> coll = summarizeAllDiagnosisEntity(this.goodRuns,
				this.badRun, this.repository);
		return rankResponsibleOptions(coll, type);
	}
	
	/**********
	 * All static methods below
	 * *******/
	
	static List<ConfDiagnosisOutput> rankResponsibleOptions(Collection<List<ConfDiagnosisEntity>> coll, RankType type) {
		if(type.equals(RankType.TFIDF_IMPORT)) {
			return rankOptionsByTfidfImportance(coll);
		} else if (type.equals(RankType.TFIDF_RATIO)) {
			return rankOptionsByTfidfRatio(coll);
		} else if (type.equals(RankType.SINGLE_RATIO)) {
			return rankOptionsByRatio(coll);
		} else if (type.equals(RankType.SINGLE_IMPORT)) {
			return rankOptionsByImportance(coll);
		} else if (type.equals(RankType.RATIO_SUM)) {
			return rankOptionsByRatioSum(coll);
		} else if (type.equals(RankType.IMPORT_SUM)) {
			return rankOptionsByImportanceSum(coll);
		} else {
			throw new Error("Unrecognized type: " + type);
		}
	}
	
	static Collection<List<ConfDiagnosisEntity>> summarizeAllDiagnosisEntity(
			Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun,
			ConfEntityRepository repository) {
		Collection<List<ConfDiagnosisEntity>> coll = new LinkedList<List<ConfDiagnosisEntity>>();
		//summarize a list of diagnosis entities for between the bad run with each good run
		for(PredicateProfileTuple goodRun : goodRuns) {
			List<ConfDiagnosisEntity> list = summarizeDiagnosisEntity(goodRun, badRun, repository);
			//do filtering
			if(MainAnalyzer.doFiltering) {
			    List<ConfDiagnosisEntity> filteredList = ProfileFilters.filter(list);
			    coll.add(filteredList);
			    System.err.println("Filtered diagnosis entities number: " + (list.size() - filteredList.size()));
			} else {
				coll.add(list);
			}
		}
		Utils.checkTrue(goodRuns.size() == coll.size());
		return coll;
	}
	
	//summarize the list of diagnosis entities between a good run with a bad run
	static List<ConfDiagnosisEntity> summarizeDiagnosisEntity(PredicateProfileTuple goodRun,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		Utils.checkTrue(goodRun.isGoodRun());
		Utils.checkTrue(!badRun.isGoodRun());
		
		//create a list of diagnosis entities
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		//go through both good profile and bad profile
		for(PredicateProfile goodProfile : goodRun.getAllProfiles()) {
			String uniqueKey = goodProfile.getUniqueKey();
			PredicateProfile badProfile = badRun.lookUpByUniqueKey(uniqueKey);
			ConfDiagnosisEntity result = new ConfDiagnosisEntity(goodProfile);
//			    = new ConfDiagnosisEntity(goodProfile.getConfigFullName(), goodProfile.getContext());
			//save attributes
			result.saveRawData(RawDataType.GOOD_EVAL_COUNT, goodProfile.getEvaluatingCount());
			result.saveRawData(RawDataType.GOOD_ENTER_COUNT, goodProfile.getEnteringCount());
			result.saveRawData(RawDataType.GOOD_RATIO, goodProfile.getRatio());
			result.saveRawData(RawDataType.GOOD_RATIO_ABS, goodProfile.absoluteRatio());
			result.saveRawData(RawDataType.GOOD_IMPORT, goodProfile.importanceValue());
			result.saveRawData(RawDataType.GOOD_IMPORT_ABS, goodProfile.absImportanceValue());
			//see the bad profile
			if(badProfile != null) {
				result.saveRawData(RawDataType.BAD_EVAL_COUNT, badProfile.getEvaluatingCount());
				result.saveRawData(RawDataType.BAD_ENTER_COUNT, badProfile.getEnteringCount());
				result.saveRawData(RawDataType.BAD_RATIO, badProfile.getRatio());
				result.saveRawData(RawDataType.BAD_RATIO_ABS, badProfile.absoluteRatio());
				result.saveRawData(RawDataType.BAD_IMPORT, badProfile.importanceValue());
				result.saveRawData(RawDataType.BAD_IMPORT_ABS, badProfile.absImportanceValue());
			}
			//add to the result
			retList.add(result);
		}
		
		for(PredicateProfile badProfile : badRun.getAllProfiles()) {
			String uniqueKey = badProfile.getUniqueKey();
			if(goodRun.lookUpByUniqueKey(uniqueKey) != null) {
				continue;
			}
			ConfDiagnosisEntity result = new ConfDiagnosisEntity(badProfile);
				//new ConfDiagnosisEntity(badProfile.getConfigFullName(), badProfile.getContext());
			result.saveRawData(RawDataType.BAD_EVAL_COUNT, badProfile.getEvaluatingCount());
			result.saveRawData(RawDataType.BAD_ENTER_COUNT, badProfile.getEnteringCount());
			result.saveRawData(RawDataType.BAD_RATIO, badProfile.getRatio());
			result.saveRawData(RawDataType.BAD_RATIO_ABS, badProfile.absoluteRatio());
			result.saveRawData(RawDataType.BAD_IMPORT, badProfile.importanceValue());
			result.saveRawData(RawDataType.BAD_IMPORT_ABS, badProfile.absImportanceValue());
			//add to the result
			retList.add(result);
		}
		
		//associate with the entity
		for(ConfDiagnosisEntity result : retList) {
			result.setConfEntity(repository);
			Utils.checkTrue(result.getConfEntity() != null);
		}
		
		//check the correctness here
		//a temp data structure to check the correctness, no affect to the return result
		Set<String> uniquePredicates = new HashSet<String>();
		for(PredicateProfile p : goodRun.getAllProfiles()) {
			uniquePredicates.add(p.getUniqueKey());
		}
		for(PredicateProfile p : badRun.getAllProfiles()) {
			uniquePredicates.add(p.getUniqueKey());
		}
		Utils.checkTrue(retList.size() == uniquePredicates.size(), "The size is not equal: "
				+ retList.size() + ", v.s., unique predicates size: " + uniquePredicates.size());
		uniquePredicates.clear();
		
		return retList;
	}
	
	/**
	 * Rank by the single highest ratio deviation
	 * */
	static List<ConfDiagnosisOutput> rankOptionsByRatio(Collection<List<ConfDiagnosisEntity>> coll) {
		Collection<List<ConfDiagnosisEntity>> rankedListsByRatio = new LinkedList<List<ConfDiagnosisEntity>>();
		for(List<ConfDiagnosisEntity> list : coll) {
			List<ConfDiagnosisEntity> rankedList = sortDiagnosisEntityByRatio(list);
			rankedListsByRatio.add(rankedList);
		}
		
		//do ranking according to the highest avg ranking
		Collection<List<ConfDiagnosisOutput>> rankedOutputs = getRankedCollectionsBySingleScore(rankedListsByRatio, ScoreType.RATIO_DELTA);
		//do the final ranking
		List<ConfDiagnosisOutput> finalRankedList = ConfDiagnosisOutput.rankByAvgRanking(rankedOutputs);
		
		return finalRankedList;
	}
	
	/**
	 * Rank by the single importance deviation
	 * */
     static List<ConfDiagnosisOutput> rankOptionsByImportance(Collection<List<ConfDiagnosisEntity>> coll) {
    	Collection<List<ConfDiagnosisEntity>> rankedListsByImport = new LinkedList<List<ConfDiagnosisEntity>>();
 		for(List<ConfDiagnosisEntity> list : coll) {
 			List<ConfDiagnosisEntity> rankedList = sortDiagnosisEntityByImportance(list);
 			rankedListsByImport.add(rankedList);
 		}
 		
 		//do ranking according to the highest avg ranking
 		Collection<List<ConfDiagnosisOutput>> rankedOutputs = getRankedCollectionsBySingleScore(rankedListsByImport, ScoreType.IMPORT_DELTA);
 		//do ranking according to the highest avg ranking
 		List<ConfDiagnosisOutput> finalRankedList = ConfDiagnosisOutput.rankByAvgRanking(rankedOutputs);

		return finalRankedList;
	}

	/**
	 * Rank by the deviated ratio deviation summation
	 * */
	static List<ConfDiagnosisOutput> rankOptionsByRatioSum(Collection<List<ConfDiagnosisEntity>> coll) {
		Collection<List<ConfDiagnosisEntity>> rankedListsByRatio = new LinkedList<List<ConfDiagnosisEntity>>();
		for(List<ConfDiagnosisEntity> list : coll) {
			List<ConfDiagnosisEntity> rankedList = sortDiagnosisEntityByRatio(list);
			rankedListsByRatio.add(rankedList);
		}
		
		Collection<List<ConfDiagnosisOutput>> rankedOutputs = getRankedCollectionsByScoreSum(rankedListsByRatio, ScoreType.RATIO_DELTA);
		List<ConfDiagnosisOutput> finalRankedList = ConfDiagnosisOutput.rankByAvgRanking(rankedOutputs);
		
		return finalRankedList;
	}
	
	/**
	 * Rank by the deviated importance deviation summation
	 * */
	static List<ConfDiagnosisOutput> rankOptionsByImportanceSum(Collection<List<ConfDiagnosisEntity>> coll) {
		Collection<List<ConfDiagnosisEntity>> rankedListsByImport = new LinkedList<List<ConfDiagnosisEntity>>();
 		for(List<ConfDiagnosisEntity> list : coll) {
 			List<ConfDiagnosisEntity> rankedList = sortDiagnosisEntityByImportance(list);
 			rankedListsByImport.add(rankedList);
 		}
 		
 		Collection<List<ConfDiagnosisOutput>> rankedOutputs = getRankedCollectionsByScoreSum(rankedListsByImport, ScoreType.IMPORT_DELTA);
		List<ConfDiagnosisOutput> finalRankedList = ConfDiagnosisOutput.rankByAvgRanking(rankedOutputs);
		
		return finalRankedList;
	}
	
	/**
	 * Rank by tf-idf across importance value
	 * */
	static List<ConfDiagnosisOutput>  rankOptionsByTfidfRatio(Collection<List<ConfDiagnosisEntity>> coll) {
		Collection<List<ConfDiagnosisEntity>> rankedListsByRatio = new LinkedList<List<ConfDiagnosisEntity>>();
		for(List<ConfDiagnosisEntity> list : coll) {
			List<ConfDiagnosisEntity> rankedList = sortDiagnosisEntityByRatio(list);
			rankedListsByRatio.add(rankedList);
		}
		
		Collection<List<ConfDiagnosisOutput>> rankedOutputs = getRankedCollectionsByScoreTfidf(rankedListsByRatio, ScoreType.RATIO_DELTA);
		List<ConfDiagnosisOutput> finalRankedList = ConfDiagnosisOutput.rankByAvgRanking(rankedOutputs);
		
		return finalRankedList;
	}

	/**
	 * Rank by tf-idf across importance value
	 * */
	static List<ConfDiagnosisOutput>  rankOptionsByTfidfImportance(Collection<List<ConfDiagnosisEntity>> coll) {
		Collection<List<ConfDiagnosisEntity>> rankedListsByImport = new LinkedList<List<ConfDiagnosisEntity>>();
 		for(List<ConfDiagnosisEntity> list : coll) {
 			List<ConfDiagnosisEntity> rankedList = sortDiagnosisEntityByImportance(list);
 			rankedListsByImport.add(rankedList);
 		}
 		
 		Collection<List<ConfDiagnosisOutput>> rankedOutputs = getRankedCollectionsByScoreTfidf(rankedListsByImport, ScoreType.IMPORT_DELTA);
		List<ConfDiagnosisOutput> finalRankedList = ConfDiagnosisOutput.rankByAvgRanking(rankedOutputs);
		
		return finalRankedList;
	}
	
	/** ***************************************
	 * Two utility methods
	 ** ***************************************/
	static List<ConfDiagnosisEntity> sortDiagnosisEntityByRatio(List<ConfDiagnosisEntity> list) {
		List<ConfDiagnosisEntity> rankedList = ConfDiagnosisEntity.rankByCriteria(list, ScoreType.RATIO_DELTA, false);
		return rankedList;
	}
	static List<ConfDiagnosisEntity> sortDiagnosisEntityByImportance(List<ConfDiagnosisEntity> list) {
		List<ConfDiagnosisEntity> rankedList = ConfDiagnosisEntity.rankByCriteria(list, ScoreType.IMPORT_DELTA, false);
		return rankedList;
	}
	
	static Collection<List<ConfDiagnosisOutput>> getRankedCollectionsBySingleScore(Collection<List<ConfDiagnosisEntity>> rankedLists,
			ScoreType t) {
		//the return collection
		Collection<List<ConfDiagnosisOutput>> rankedOutputs = new LinkedList<List<ConfDiagnosisOutput>>();
		//iterate through each ranked list
		for(List<ConfDiagnosisEntity> rankedList : rankedLists) {
			List<ConfDiagnosisOutput> rankedOutput = new LinkedList<ConfDiagnosisOutput>();
			//map from the configuration option name to the output
			Map<String, ConfDiagnosisOutput> visitedConfigs = new LinkedHashMap<String, ConfDiagnosisOutput>();
			int ranking = 0;
			for(ConfDiagnosisEntity rankedEntity : rankedList) {
				ranking++;
				String configName = rankedEntity.getConfigFullName(); //no context here
				if(visitedConfigs.containsKey(configName)) {
					visitedConfigs.get(configName).addExplain("Omit Rank: " + ranking
							+ ": " + configName + " at context : " + rankedEntity.getContext()
							+ ",  " + t.name() + ": " + rankedEntity.getScore(t)
							+ ", its provence: " + rankedEntity.getScoreProvenance(t));
				} else {
					//initially create and add the output
					ConfDiagnosisOutput output = new ConfDiagnosisOutput(rankedEntity.getConfEntity());
					visitedConfigs.put(configName, output);
					output.addExplain("Choose Rank: " + ranking
							+ ": " + configName + " at context : " + rankedEntity.getContext()
							+ ",  " + t.name() + ": " + rankedEntity.getScore(t)
							+ ", its provence: " + rankedEntity.getScoreProvenance(t));
					rankedOutput.add(output);
				}
			}
			//add to the output
			rankedOutputs.add(rankedOutput);
		}
		return rankedOutputs;
	}
	
	static Collection<List<ConfDiagnosisOutput>> getRankedCollectionsByScoreSum(Collection<List<ConfDiagnosisEntity>> rankedLists,
			ScoreType t) {
		Collection<List<ConfDiagnosisOutput>> rankedOutputs = new LinkedList<List<ConfDiagnosisOutput>>();
		for(List<ConfDiagnosisEntity> rankedList : rankedLists) {
			//from configuration full name to output
			Map<String, ConfDiagnosisOutput> visitedConfigs = new LinkedHashMap<String, ConfDiagnosisOutput>();
			//from the output to the float
			Map<ConfDiagnosisOutput, Float> configOutputSums = new LinkedHashMap<ConfDiagnosisOutput, Float>();
			for(ConfDiagnosisEntity rankedEntity : rankedList) {
				String configName = rankedEntity.getConfigFullName();
				if(visitedConfigs.containsKey(configName)) {
					ConfDiagnosisOutput output = visitedConfigs.get(configName);
					Utils.checkTrue(configOutputSums.containsKey(output), configName + " is not contained.");
					Float updatedValue = configOutputSums.get(output) + rankedEntity.getScore(t);
					visitedConfigs.get(configName).addExplain("Add new score: " + rankedEntity.getScore(t)
							+ "  at context: " + rankedEntity.getContext() + ", now updated value: " + updatedValue);
					configOutputSums.put(output, updatedValue);
				} else {
					ConfDiagnosisOutput output = new ConfDiagnosisOutput(rankedEntity.getConfEntity());
					visitedConfigs.put(configName, output);
					output.addExplain("First add new score: " + rankedEntity.getScore(t)
							+ "  at context: " + rankedEntity.getContext());
					configOutputSums.put(output, rankedEntity.getScore(t));
				}
			}
			//add to the output
			List<ConfDiagnosisOutput> sortedEntities = Utils.sortByValueAndReturnKeys(configOutputSums, false);
//			System.err.println(configOutputSums);
//			System.err.println(sortedEntities);
			rankedOutputs.add(sortedEntities);
		}
		return rankedOutputs;
	}
	
	static Collection<List<ConfDiagnosisOutput>> getRankedCollectionsByScoreTfidf(Collection<List<ConfDiagnosisEntity>> rankedLists,
			ScoreType t) {
		Collection<List<ConfDiagnosisOutput>> rankedOutputs = new LinkedList<List<ConfDiagnosisOutput>>();
		for(List<ConfDiagnosisEntity> rankedList : rankedLists) {
			Map<String, ConfDiagnosisOutput> createdOutputMap = new LinkedHashMap<String, ConfDiagnosisOutput>();
			//do the tf-idf algorithm here
			for(ConfDiagnosisEntity entity : rankedList) {
				String config = entity.getConfigFullName();
				if(!createdOutputMap.containsKey(config)) {
					createdOutputMap.put(config, new ConfDiagnosisOutput(entity.getConfEntity()));
				}
			}
			
			//the map to store all tf idf values
			Map<ConfDiagnosisOutput, Float> tfidfValues = new LinkedHashMap<ConfDiagnosisOutput, Float>();
			for(String key : createdOutputMap.keySet()) {
				tfidfValues.put(createdOutputMap.get(key), 0.0f);
			}
			//compute values
			for(ConfDiagnosisEntity entity : rankedList) {
				String config = entity.getConfigFullName();
				String context = entity.getContext();
				Float score = entity.getScore(t);
				int numOfShares = 0;
				for(ConfDiagnosisEntity e : rankedList) {
					if(e.getContext().equals(context)) {
						numOfShares ++;
					}
				}
				Utils.checkTrue(numOfShares > 0);
				ConfDiagnosisOutput o =createdOutputMap.get(config);
				Float myScore = score/numOfShares;
				o.addExplain("num of shares: " + numOfShares + ",  original tfidf value: " + tfidfValues.get(o)
						+ ", adding score: " + myScore + ", becomes: " + (tfidfValues.get(o) + myScore));
				tfidfValues.put(o, tfidfValues.get(o) + myScore);
			}
			
			List<ConfDiagnosisOutput> outputList = Utils.sortByValueAndReturnKeys(tfidfValues, false);
			rankedOutputs.add(outputList);
		}
		return rankedOutputs;
	}
}