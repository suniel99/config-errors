package edu.washington.cs.conf.diagnosis;

public class ProfileDistanceCalculator {
	
	public enum DistanceType {HAMMING, LEVENSHTEIN, EUCLIDEAN, JACCARD}
	
	public static float computeDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2, DistanceType type) {
		if(type.equals(DistanceType.HAMMING)) {
			return computeHammingDistance(t1, t2);
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
	
	public static float computeHammingDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		return 0.0f;
	}
	
	public static float computeLevenshteinDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		return 0.0f;
	}
	
	public static float computeEuclideanDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		return 0.0f;
	}
	
	public static float computeJaccardDistance(PredicateProfileTuple t1,
			PredicateProfileTuple t2) {
		return 0.0f;
	}
}
