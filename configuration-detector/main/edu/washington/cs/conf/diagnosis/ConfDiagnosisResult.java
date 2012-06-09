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

public class ConfDiagnosisResult {
	
	private final String configFullName;
	private final String context;
	//store different scoring criteria and the corresponding score
	private final Map<String, Float> scores = new HashMap<String, Float>();

    public ConfDiagnosisResult(String configFullName, String context) {
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

    public ConfEntity getConfEntity(ConfEntityRepository repo) {
    	return repo.lookupConfEntity(this.configFullName);
    }
    
    public void saveScore(String criteria, Float score) {
    	Utils.checkTrue(!this.scores.containsKey(criteria));
    	this.scores.put(criteria, score);
    }
    
    public Float getScore(String criteria) {
    	return scores.get(criteria);
    }
    
    public boolean hasScore(String critera) {
    	return scores.containsKey(critera);
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof ConfDiagnosisResult)) {
    		return false;
    	}
    	ConfDiagnosisResult confE = (ConfDiagnosisResult)obj;
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
    public static List<ConfDiagnosisResult> rankByCriteria(Collection<ConfDiagnosisResult> results, String criteria,
    		boolean increase) {
    	//check the existence of the criteria
    	for(ConfDiagnosisResult result : results) {
    		Utils.checkTrue(result.hasScore(criteria));
    	}
    	Map<ConfDiagnosisResult, Float> scoreMap = new LinkedHashMap<ConfDiagnosisResult, Float>();
    	for(ConfDiagnosisResult result : results) {
    		scoreMap.put(result, result.getScore(criteria));
    	}
    	List<ConfDiagnosisResult> rankedList = Utils.sortByValueAndReturnKeys(scoreMap, increase);
    	return rankedList;
    }
}