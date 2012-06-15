package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements some heuristic to filter unlikely (or likely not useful) profiles.
 * */
public class ProfileFilters {
	
	public static List<ConfDiagnosisEntity> filter(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> filtered = filterSingleOccurance(entities);
		filtered = filterOneOccurance(filtered);
		filtered = filterSameRatio(filtered);
		filtered = filterSameCountDelta(filtered);
		return filtered;
	}
	
	/**
	 * Each individual filters
	 * */
	public static List<ConfDiagnosisEntity> filterSingleOccurance(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.isSingleOccurance()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	public static List<ConfDiagnosisEntity> filterOneOccurance(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.missedByOneRun()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	public static List<ConfDiagnosisEntity> filterSameRatio(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.hasSameRatio()) {
				retList.add(entity);
			}
		}
		return retList;
	}
	
	public static List<ConfDiagnosisEntity> filterSameCountDelta(Collection<ConfDiagnosisEntity> entities) {
		List<ConfDiagnosisEntity> retList = new LinkedList<ConfDiagnosisEntity>();
		for(ConfDiagnosisEntity entity : entities) {
			if(!entity.hasSameCountDelta()) {
				retList.add(entity);
			}
		}
		return retList;
	}

}