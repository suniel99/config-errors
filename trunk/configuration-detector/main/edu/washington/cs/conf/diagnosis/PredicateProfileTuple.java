package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * It consists a list of predicate profiles, like a
 * database tuple which consists of a list of cell values.
 * 
 * Here, a cell value is akin to a "PredicateProfile" object
 * */
public class PredicateProfileTuple {

	private final List<PredicateProfile> profiles
	    = new LinkedList<PredicateProfile>();
	
	public PredicateProfileTuple(Collection<PredicateProfile> coll) {
		this.profiles.addAll(coll);
	}
	
	public void addProfile(PredicateProfile profile) {
		this.profiles.add(profile);
	}
}