package edu.washington.cs.conf.diagnosis;

import instrument.Globals;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.util.Utils;

public class ConfDiagnosisEntity {
	
	public enum RawDataType{GOOD_EVAL_COUNT, GOOD_ENTER_COUNT, GOOD_IMPORT, GOOD_RATIO, GOOD_RATIO_ABS, GOOD_IMPORT_ABS,
		BAD_EVAL_COUNT, BAD_ENTER_COUNT, BAD_IMPORT, BAD_RATIO, BAD_RATIO_ABS, BAD_IMPORT_ABS};
		
	//ratio only calcuates the percentage of passing / all
	//import metric only balances sensitivty and specificity
	public enum ScoreType {RATIO_DELTA, IMPORT_DELTA}
	
	private final String configFullName;
	private final String context;
	//store different scoring criteria and the corresponding score
	private final Map<RawDataType, Object> rawData = new HashMap<RawDataType, Object>();
	//all computed scores
	private final Map<ScoreType, Float> scores = new HashMap<ScoreType, Float>();
	
	private ConfEntity entity = null;

	public ConfDiagnosisEntity(PredicateProfile profile) {
		String configFullName = profile.getConfigFullName();
		String context = profile.getContext();
		Utils.checkNotNull(configFullName);
    	Utils.checkNotNull(context);
    	this.configFullName = configFullName;
    	this.context = context;
	}
	
//    public ConfDiagnosisEntity(String configFullName, String context) {
//    	Utils.checkNotNull(configFullName);
//    	Utils.checkNotNull(context);
//    	this.configFullName = configFullName;
//    	this.context = context;
//    }
    
    public String getConfigFullName() {
    	return this.configFullName;
    }
    
    public String getContext() {
    	return this.context;
    }

    public void setConfEntity(ConfEntityRepository repo) {
    	this.entity = repo.lookupConfEntity(this.configFullName);
    }
    
    public ConfEntity getConfEntity() {
    	return this.entity;
    }
    
    public void saveRawData(RawDataType criteria, Object score) {
    	Utils.checkTrue(!this.rawData.containsKey(criteria));
    	this.rawData.put(criteria, score);
    }
    
    public Float getRawData(RawDataType criteria) {
    	return Float.parseFloat(rawData.get(criteria).toString());
    }
    
    public boolean hasRawData(RawDataType critera) {
    	return rawData.containsKey(critera);
    }
    
//    public void saveScore(ScoreType type, Float score) {
//    	Utils.checkTrue(!this.scores.containsKey(type));
//    	this.scores.put(type, score);
//    }
    
    public Float getScore(ScoreType type) {
    	return this.scores.get(type);
    }
    
    public boolean hasScore(ScoreType type) {
    	return this.scores.containsKey(type);
    }
    
    public void computeAllScores() {
    	this.computeScore(ScoreType.RATIO_DELTA);
    	this.computeScore(ScoreType.IMPORT_DELTA);
    }
    
    public void computeScore(ScoreType type) {
    	if(type.equals(ScoreType.RATIO_DELTA)) {
    		boolean hasGoodRatio = this.rawData.containsKey(RawDataType.GOOD_RATIO);
    		boolean hasBadRatio = this.rawData.containsKey(RawDataType.BAD_RATIO);
    		Utils.checkTrue( hasGoodRatio || hasBadRatio );
    		if(hasGoodRatio && hasBadRatio ) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_RATIO) - this.getRawData(RawDataType.BAD_RATIO));
    			this.scores.put(ScoreType.RATIO_DELTA, score);
    		} else if (hasGoodRatio && !hasBadRatio) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_RATIO_ABS));
    			this.scores.put(ScoreType.RATIO_DELTA, score);
    		} else if (!hasGoodRatio && hasBadRatio) {
    			float score = Math.abs(this.getRawData(RawDataType.BAD_RATIO_ABS));
    			this.scores.put(ScoreType.RATIO_DELTA, score);
    		} else {
    			throw new Error();
    		}
    	} else if (type.equals(ScoreType.IMPORT_DELTA)) {
    		boolean hasGoodImport = this.rawData.containsKey(RawDataType.GOOD_IMPORT);
    		boolean hasBadImport = this.rawData.containsKey(RawDataType.BAD_IMPORT);
            if(hasGoodImport && hasBadImport ) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_IMPORT) - this.getRawData(RawDataType.BAD_IMPORT));
    			this.scores.put(ScoreType.IMPORT_DELTA, score);
    		} else if (hasGoodImport && !hasBadImport) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_IMPORT_ABS));
    			this.scores.put(ScoreType.IMPORT_DELTA, score);
    		} else if (!hasGoodImport && hasBadImport) {
    			float score = Math.abs(this.getRawData(RawDataType.BAD_IMPORT_ABS));
    			this.scores.put(ScoreType.IMPORT_DELTA, score);
    		} else {
    			throw new Error();
    		}
    	} else {
    		throw new Error("Unrecognized: " + type);
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof ConfDiagnosisEntity)) {
    		return false;
    	}
    	ConfDiagnosisEntity confE = (ConfDiagnosisEntity)obj;
    	return this.configFullName.equals(confE.configFullName)
    	    && this.context.equals(confE.context)
    	    && this.rawData.equals(confE.rawData)
    	    && this.scores.equals(confE.scores);
    }
    
    @Override
    public int hashCode() {
    	return this.configFullName.hashCode() + 13*this.context.hashCode() + 29*this.rawData.hashCode()
    	     + 31*this.scores.hashCode();
    }
    
    @Override
    public String toString() {
    	return this.configFullName + "@" + this.context + Globals.lineSep + "   " + this.rawData
    	    + Globals.lineSep + "   " + this.scores
    	    + Globals.lineSep + "   config entity: " + this.entity;
    }
    
    /**
     * A utility method for sorting config by its score
     * */
    public static List<ConfDiagnosisEntity> rankByCriteria(Collection<ConfDiagnosisEntity> results, ScoreType scoreType,
    		boolean increase) {
    	//check the existence of the scoreType
    	for(ConfDiagnosisEntity result : results) {
    		if(!result.hasScore(scoreType)) {
    			result.computeScore(scoreType);
    			Utils.checkTrue(result.hasScore(scoreType));
    		}
    	}
    	//do the ranking here
    	Map<ConfDiagnosisEntity, Float> scoreMap = new LinkedHashMap<ConfDiagnosisEntity, Float>();
    	for(ConfDiagnosisEntity result : results) {
    		scoreMap.put(result, result.getScore(scoreType));
    	}
    	List<ConfDiagnosisEntity> rankedList = Utils.sortByValueAndReturnKeys(scoreMap, increase);
    	return rankedList;
    }
}