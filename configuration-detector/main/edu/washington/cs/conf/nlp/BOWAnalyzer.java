package edu.washington.cs.conf.nlp;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

/**
 * This implements the bag of word model.
 * */
public class BOWAnalyzer {

	//with the use of tf-idf
	public final TFIDFWeightCalculator tfidf;
	
	public final WordNetReader reader = new WordNetReader();
	
	public BOWAnalyzer(TFIDFWeightCalculator tfidf) {
		Utils.checkNotNull(tfidf);
		this.tfidf = tfidf;
		this.tfidf.computeTFIDFValues();
	}
	
	public Float computeSimilarity(String s1, String s2) {
		String[] words1 = NLPUtils.extractWords(s1);
		Set<String> set1 = new LinkedHashSet<String>(Arrays.asList(words1));
		String[] words2 = NLPUtils.extractWords(s2);
		Set<String> set2 = new LinkedHashSet<String>(Arrays.asList(words2));
		
		return 0.5f * (
				computeOneWaySimilarity(set1, words1, set2, words2)
				+
				computeOneWaySimilarity(set2, words2, set1, words1)
				);
	}
	
	//
	private Float computeOneWaySimilarity(Set<String> set1, String[] words1, Set<String> set2, String[] words2) {
		float sum = 0.0f;
		for(String w1 : words1) {
			sum += this.tfidf.getIDFValue(w1);
		}
		float overlap = 0.0f;
		for(String w1 : words1) {
			float simValue = maxSim(w1, set2);
			float idfValue = this.tfidf.getIDFValue(w1);
			overlap += simValue * idfValue;
		}
		return overlap / sum;
	}
	
	private Float maxSim(String word, Set<String> words) {
		return words.contains(word) ? 1.0f : 0.0f;
	}
}