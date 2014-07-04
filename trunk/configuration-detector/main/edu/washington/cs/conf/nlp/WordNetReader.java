package edu.washington.cs.conf.nlp;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

//get the synonym for some words
public class WordNetReader {
	
	private static IDictionary dict = null;
	
	public static IDictionary getDict() {
		if(dict == null) {
			String wnhome = "D:\\develop-tools\\wordnet\\";
			String path = wnhome + File.separator + "dict";
			try {
			    URL url = new URL("file", null , path );
			    //construct the dictionary object and open it
			    dict = new Dictionary ( url);
			    dict.open();
			} catch (Throwable e) {
				throw new Error(e);
			}
		}
		return dict;
	}
	
	private static POS[] getPos() {
		return new POS[]{POS.NOUN, POS.ADJECTIVE, POS.ADVERB, POS.VERB};
	}
	
	public static Collection<String> getSyn(String aWord) {
		Set<String> words = new HashSet<String>();
		//get the dictionary
		IDictionary dict = getDict();
		for(POS pos : getPos()) {
		    IIndexWord idxWord = dict.getIndexWord (aWord, pos );
		    if(idxWord == null) {
		    	continue;
		    }
		    IWordID wordID = idxWord.getWordIDs().get(0) ; // 1st meaning
		    IWord word = dict.getWord (wordID);
		    ISynset synset = word.getSynset ();
		    // iterate over words associated with the synset
		    for( IWord w : synset.getWords ()) {
			    if(w.getLemma().equals(aWord)) {
				    continue;
			    }
			    words.add(w.getLemma());
		    }
		}
		
		return words;
	}

}