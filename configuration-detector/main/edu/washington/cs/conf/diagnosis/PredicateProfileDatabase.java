package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;

public class PredicateProfileDatabase {
	
	private final List<PredicateProfileTuple> tuples
	    = new LinkedList<PredicateProfileTuple>();
	
	public PredicateProfileDatabase(Collection<PredicateProfileTuple> coll) {
		this.tuples.addAll(coll);
	}
	
	public void addTuple(PredicateProfileTuple tuple) {
		this.tuples.add(tuple);
	}
	
	public List<PredicateProfileTuple> getAllTuples() {
		return this.tuples;
	}
	
	/**
	 * This returns a list of similar tuples
	 * */
	public List<PredicateProfileTuple> findSimilarTuples(PredicateProfileTuple target, DistanceType t,
			float threashold) {
		List<PredicateProfileTuple> retList = new LinkedList<PredicateProfileTuple>();
		
		for(PredicateProfileTuple tuple : tuples) {
			float distance = ProfileDistanceCalculator.computeDistance(tuple, target, t);
			if(distance >= threashold) {
				retList.add(tuple);
			}
		}
		
		return retList;
	}
}