package edu.washington.cs.conf.diagnosis;

import java.util.HashSet;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

/**
 * A class to calculate multiple types of distances
 * */
public class ProfileDistanceCalculator {
	
	public enum DistanceType {MANHATTAN, LEVENSHTEIN, EUCLIDEAN, JACCARD}
	
	public static float computeDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2, DistanceType type) {
		if(type.equals(DistanceType.MANHATTAN)) {
			return computeManhattanDistance(t1, t2);
		} else if (type.equals(DistanceType.LEVENSHTEIN)) {
			return computeLevenshteinDistance(t1, t2);
		} else if (type.equals(DistanceType.EUCLIDEAN)) {
			return computeEuclideanDistance(t1, t2);
		} else if (type.equals(DistanceType.JACCARD)) {
			return computeJaccardDistance(t1, t2);
		} else {
			throw new Error("Unsupported type: " + type);
		}
	}
	
	/**
	 * see: http://en.wikipedia.org/wiki/Manhattan_distance
	 * */
	public static float computeManhattanDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		Utils.checkNotNull(t1);
		Utils.checkNotNull(t2);
		if(t1.equals(t2)) {
			return 0.0f;
		}
		Set<String> allKeys = getAllUniqueKeys(t1, t2);
		Float distance = 0.0f;
		for(String key : allKeys) {
			PredicateProfile p1 = t1.lookUpByUniqueKey(key);
			PredicateProfile p2 = t2.lookUpByUniqueKey(key);
			Utils.checkTrue(p1 != null || p2 != null);
			float delta = 0.0f;
			if(p1 != null && p2 != null) {
				delta = Math.abs(p1.getRatio() - p2.getRatio());
			} else if (p1 == null && p2 != null) {
				delta = p2.absoluteValue();
			} else { //p1 != null && p2 == null
				delta = p1.absoluteValue();
			}
			distance += delta;
		}
		
		return distance;
	}
	
	/**
	 * see: http://en.wikipedia.org/wiki/Damerau%E2%80%93Levenshtein_distance
	 * does not make sense here!
	 * */
	public static float computeLevenshteinDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		Utils.checkNotNull(t1);
		Utils.checkNotNull(t2);
		if(t1.equals(t2)) {
			return 0.0f;
		}
		throw new Error();
//		return 0.0f;
	}
	
	/**
	 * see: http://en.wikipedia.org/wiki/Euclidean_distance
	 * */
	public static float computeEuclideanDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		Utils.checkNotNull(t1);
		Utils.checkNotNull(t2);
		if(t1.equals(t2)) {
			return 0.0f;
		}
		Set<String> allKeys = getAllUniqueKeys(t1, t2);
		Float distance = 0.0f;
		for(String key : allKeys) {
			PredicateProfile p1 = t1.lookUpByUniqueKey(key);
			PredicateProfile p2 = t2.lookUpByUniqueKey(key);
			Utils.checkTrue(p1 != null || p2 != null);
			float delta = 0.0f;
			if(p1 != null && p2 != null) {
				delta = Math.abs(p1.getRatio() - p2.getRatio());
			} else if (p1 == null && p2 != null) {
				delta = p2.absoluteValue();
			} else { //p1 != null && p2 == null
				delta = p1.absoluteValue();
			}
			float d = delta*delta; //compute the square
			distance += d;
		}
		return (float)Math.sqrt((double)distance);
	}
	
	/**
	 * see: http://en.wikipedia.org/wiki/Jaccard_index
	 * */
	public static float computeJaccardDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		Utils.checkNotNull(t1);
		Utils.checkNotNull(t2);
		if(t1.equals(t2)) {
			return 0.0f;
		}
		Set<String> allKeys = getAllUniqueKeys(t1, t2);
		
		float intersect = 0.0f;
		float union = 0.0f;
		for(String key : allKeys) {
			PredicateProfile p1 = t1.lookUpByUniqueKey(key);
			PredicateProfile p2 = t2.lookUpByUniqueKey(key);
			Utils.checkTrue(p1 != null || p2 != null);
			if(p1 != null && p2 != null) {
				float r1 = p1.getRatio();
				float r2 = p2.getRatio();
				float min = Math.min(r1, r2);
				intersect += min;
				union += min;
			} else if (p1 == null && p2 != null) {
				float r2 = p2.getRatio();
				union += r2;
			} else { //p1 != null && p2 == null
				float r1 = p1.getRatio();
				union += r1;
			}
		}
		
		float distance = 1 - (intersect / union);
		Utils.checkTrue(distance <= 1.0f);
		return distance;
	}
	
	private static Set<String> getAllUniqueKeys(PredicateProfileTuple t1, PredicateProfileTuple t2) {
		Set<String> t = new HashSet<String>();
		t.addAll(t1.getAllUniqueKeys());
		t.addAll(t2.getAllUniqueKeys());
		return t;
	}
}
