package edu.washington.cs.conf.nlp;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

//given a set of sentences, calculate the weight of each word
//in each sentence
public class TFIDFWeightCalculator {

	//each line is like a document
	public final List<String[]> wordLine;
	
	private Map<String, Float> idfValueMap = new LinkedHashMap<String, Float>();
	
	public TFIDFWeightCalculator(Collection<String> coll) {
		this.wordLine = new LinkedList<String[]>();
		for(String s : coll) {
			String[] words = extractWords(s);
			wordLine.add(words);
		}
//		System.out.println("Line number: " + wordLine.size());
	}
	
	public void computeTFIDFValues() {
		//then count if the word appears in a sentence
		Map<String, Integer> allWordsFreq = new LinkedHashMap<String, Integer>();
		for(String[] ws : wordLine) {
			for(String w : ws) {
				allWordsFreq.put(w, 0);
			}
		}
		//then go through each line
		for(String[] ws : wordLine) {
			Set<String> set = new LinkedHashSet<String>();
			set.addAll(Arrays.asList(ws));
			for(String w : set) {
				allWordsFreq.put(w, allWordsFreq.get(w) + 1);
			}
		}
		
		//then add to the idfValuemap
		int totalLine = wordLine.size();
		for(String word : allWordsFreq.keySet()) {
			int freq = allWordsFreq.get(word);
			float idfValue = (float)totalLine / (float)freq;
			this.idfValueMap.put(word, idfValue);
		}
	}
	
	public Float getIDFValue(String word) {
		if(!idfValueMap.keySet().contains(word)) {
			return valueForNonExistence();
		} else {
			return idfValueMap.get(word);
		}
	}
	
	//a naive, unoptimized implementation
	public int getTFValue(String word, int docIndex) {
		String[] doc = this.wordLine.get(docIndex);
		int c = 0;
		for(String w : doc) {
			if(w.equals(word)) {
				c++;
			}
		}
		return c;
	}
	
	public Set<String> getAllWords() {
		return this.idfValueMap.keySet();
	}
	
	public void printIDFValueDescreasingly() {
		Map<String, Float> sortedMap = Utils.sortByValue(this.idfValueMap, false);
		for(String w : sortedMap.keySet()) {
			System.out.println(w + " : " + sortedMap.get(w));
		}
	}
	
	private Float valueForNonExistence() {
		return 0.0f;
	}
	
	//need to filter away common words
	private String[] extractWords(String line) {
		String[] words = line.split("\\s+");
		for(int i = 0; i < words.length; i++) {
			words[i] = words[i].toLowerCase();
		}
		return words;
	}
	
}