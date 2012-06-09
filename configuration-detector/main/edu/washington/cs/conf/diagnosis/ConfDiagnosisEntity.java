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
	
	public enum ProfilePosition{GOOD_EVAL_COUNT, GOOD_ENTER_COUNT, GOOD_IMPORT, GOOD_RATIO,
		BAD_EVAL_COUNT, BAD_ENTER_COUNT, BAD_IMPORT, BAD_RATIO};
	
	private final String configFullName;
	private final String context;
	//store different scoring criteria and the corresponding score
	private final Map<String, Object> scores = new HashMap<String, Object>();
	
	private ConfEntity entity = null;

    public ConfDiagnosisEntity(String configFullName, String context) {
    	Utils.checkNotNull(configFullName);
    	Utils.checkNotNull(context);
    	this.configFullName = configFullName;
    	this.context = context;
    }
    
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
    
    public void saveScore(String criteria, Object score) {
    	Utils.checkTrue(!this.scores.containsKey(criteria));
    	this.scores.put(criteria, score);
    }
    
    public Float getScore(String criteria) {
    	return Float.parseFloat(scores.get(criteria).toString());
    }
    
    public boolean hasScore(String critera) {
    	return scores.containsKey(critera);
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof ConfDiagnosisEntity)) {
    		return false;
    	}
    	ConfDiagnosisEntity confE = (ConfDiagnosisEntity)obj;
    	return this.configFullName.equals(confE.configFullName)
    	    && this.context.equals(confE.context)
    	    && this.scores.equals(confE.scores);
    }
    
    @Override
    public int hashCode() {
    	return this.configFullName.hashCode() + 13*this.context.hashCode() + 29*this.scores.hashCode();
    }
    
    @Override
    public String toString() {
    	return this.configFullName + "@" + this.context + Globals.lineSep + "   " + this.scores;
    }
    
    /**
     * A utility method for sorting config by its score
     * */
    public static List<ConfDiagnosisEntity> rankByCriteria(Collection<ConfDiagnosisEntity> results, String criteria,
    		boolean increase) {
    	//check the existence of the criteria
    	for(ConfDiagnosisEntity result : results) {
    		Utils.checkTrue(result.hasScore(criteria));
    	}
    	Map<ConfDiagnosisEntity, Float> scoreMap = new LinkedHashMap<ConfDiagnosisEntity, Float>();
    	for(ConfDiagnosisEntity result : results) {
    		scoreMap.put(result, result.getScore(criteria));
    	}
    	List<ConfDiagnosisEntity> rankedList = Utils.sortByValueAndReturnKeys(scoreMap, increase);
    	return rankedList;
    }
}