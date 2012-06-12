package edu.washington.cs.conf.diagnosis;

import instrument.Globals;

import java.util.HashSet;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

/**
 * A class to calculate multiple types of distances
 * */
public class ProfileDistanceCalculator {
	
	public enum DistanceType {MANHATTAN, LEVENSHTEIN, EUCLIDEAN, JACCARD, INTERPRODUCT, SUBTRACTION}
	
	public static float computeDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2, DistanceType type) {
		if(type.equals(DistanceType.MANHATTAN)) {
			return computeManhattanDistance(t1, t2);
		} else if (type.equals(DistanceType.INTERPRODUCT)) {
			return computeInterproductDistance(t1, t2);
		} else if (type.equals(DistanceType.SUBTRACTION)) {
			return computeSubstractionDistance(t1, t2);
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
	
	public static float computeInterproductDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		Utils.checkNotNull(t1);
		Utils.checkNotNull(t2);
		if(t1.equals(t2)) {
			return 0.0f;
		}
		
		Set<String> allKeys = getAllUniqueKeys(t1, t2);
		Float similarity_sum = 0.0f;
		for(String key : allKeys) {
			PredicateProfile p1 = t1.lookUpByUniqueKey(key);
			PredicateProfile p2 = t2.lookUpByUniqueKey(key);
			Utils.checkTrue(p1 != null || p2 != null);
			float delta = 0.0f;
			if(p1 != null && p2 != null) {
//				delta = Math.abs(p1.getRatio() - p2.getRatio());
				if(p1.getRatio() == 0 && p2.getRatio() ==0) {
					delta = 1.0f;
				} else {
				    float singleSimilar = p1.getRatio() > p2.getRatio() ? p2.getRatio()/p1.getRatio() : p1.getRatio() / p2.getRatio();
				    delta = singleSimilar;
				}
			} else if (p1 == null && p2 != null) {
//				delta = p2.absoluteRatio();
				delta = 0f;
			} else { //p1 != null && p2 == null
//				delta = p1.absoluteRatio();
				delta = 0f;
			}
//			System.out.println(delta);
			similarity_sum += delta*delta;
		}
//		System.out.println(similarity_sum);
		Float distance = 1 - (similarity_sum / allKeys.size());
//		Float distance = (float) (1 - (Math.sqrt((double)similarity_sum) / allKeys.size()));
		return distance;
	}
	
	public static float computeSubstractionDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		Utils.checkNotNull(t1);
		Utils.checkNotNull(t2);
		if(t1.equals(t2)) {
			return 0.0f;
		}
		
		Set<String> allKeys = getAllUniqueKeys(t1, t2);
		Float total = 0.0f;
		for(String key : allKeys) {
			PredicateProfile p1 = t1.lookUpByUniqueKey(key);
			PredicateProfile p2 = t2.lookUpByUniqueKey(key);
			Utils.checkTrue(p1 != null || p2 != null);
			float delta = 0.0f;
			if(p1 != null && p2 != null) {
				delta = Math.abs(p1.getRatio() - p2.getRatio());
			} else if (p1 == null && p2 != null) {
				delta = p2.absoluteRatio();
			} else { //p1 != null && p2 == null
				delta = p1.absoluteRatio();
			}
			total += delta;
		}
		
		Float distance = total / allKeys.size();
		return distance;
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
				delta = p2.absoluteRatio();
			} else { //p1 != null && p2 == null
				delta = p1.absoluteRatio();
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
				delta = p2.absoluteRatio();
			} else { //p1 != null && p2 == null
				delta = p1.absoluteRatio();
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
	
	public static void showAlignedVectors(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		Utils.checkNotNull(t1);
		Utils.checkNotNull(t2);
		
		StringBuilder sb = new StringBuilder();
		
		Set<String> allKeys = getAllUniqueKeys(t1, t2);
		for(String key : allKeys) {
			PredicateProfile p1 = t1.lookUpByUniqueKey(key);
			PredicateProfile p2 = t2.lookUpByUniqueKey(key);
			sb.append(key + Globals.lineSep + "   :");
			Utils.checkTrue(p1 != null || p2 != null);
			if(p1 != null && p2 != null) {
				sb.append(p1.getRatio() + ", " + p1.getRatio());
			} else if (p1 == null && p2 != null) {
				sb.append("NaN, " + p2.absoluteRatio());
			} else { //p1 != null && p2 == null
				sb.append(p1.absoluteRatio() + ", NaN");
			}
			sb.append(Globals.lineSep);
		}
		System.out.println(sb.toString());
	}
}
