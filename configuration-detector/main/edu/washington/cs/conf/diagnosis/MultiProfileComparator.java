package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

public class MultiProfileComparator {

	//compare multiple good profiles with a single bad profile
	public List<Collection<PredicateProfile>> goodProfilesList
	    = new LinkedList<Collection<PredicateProfile>>();
	//a single bad profile
	public final Collection<PredicateProfile> badProfiles;
	
	public MultiProfileComparator(Collection<PredicateProfile> badProfiles) {
		Utils.checkNotNull(badProfiles);
		this.badProfiles = badProfiles;
	}
	
	public List<String> rankProfiles() {
		//TODO use the majority vote algorithm
		return null;
	}
	
	private List<String> getRankedProfileUsingSingleGoodRun(Collection<PredicateProfile> goodProfiles,
			Collection<PredicateProfile> badProfiles) {
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
		return comparator.findProfilesByTfIdf();
	}
}
