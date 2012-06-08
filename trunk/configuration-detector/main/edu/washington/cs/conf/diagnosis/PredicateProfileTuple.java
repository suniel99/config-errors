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

	/**use which run to get the following profiles */
	public final String name;
	
	private final List<PredicateProfile> profiles
	    = new LinkedList<PredicateProfile>();
	
	public PredicateProfileTuple(String name, Collection<PredicateProfile> coll) {
		this.name = name;
		this.profiles.addAll(coll);
	}
	
	public void addProfile(PredicateProfile profile) {
		this.profiles.add(profile);
	}
}
