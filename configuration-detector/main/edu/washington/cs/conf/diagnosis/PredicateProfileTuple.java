package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

/**
 * It consists a list of predicate profiles, like a
 * database tuple which consists of a list of cell values.
 * 
 * Here, a cell value is akin to a "PredicateProfile" object
 * */
public class PredicateProfileTuple {

	/**use which run to get the following profiles */
	public final String name;
	
	public final boolean isGoodRun;
	
	private final List<PredicateProfile> profiles
	    = new LinkedList<PredicateProfile>();
	
	public static PredicateProfileTuple createGoodRun(String name, Collection<PredicateProfile> coll) {
		return new PredicateProfileTuple(name, coll, true);
	}
	
	public static PredicateProfileTuple createBadRun(String name, Collection<PredicateProfile> coll) {
		return new PredicateProfileTuple(name, coll, false);
	}
	
	PredicateProfileTuple(String name, Collection<PredicateProfile> coll, boolean isGoodRun) {
		this.name = name;
		this.profiles.addAll(coll);
		this.isGoodRun = isGoodRun;
		this.checkValidity();
	}
	
	public boolean isGoodRun() {
		return this.isGoodRun;
	}
	
//	public void addProfile(PredicateProfile profile) {
//		this.profiles.add(profile);
//	}
	
	public List<PredicateProfile> getAllProfiles() {
		return this.profiles;
	}
	
	public PredicateProfile lookUpByUniqueKey(String key) {
		Utils.checkNotNull(key);
		for(PredicateProfile p : this.profiles) {
			if(p.getUniqueKey().equals(key)) {
				return p;
			}
		}
		return null;
	}
	
	public Set<String> getAllUniqueKeys() {
		Set<String> keys = new HashSet<String>();
		for(PredicateProfile p : this.profiles) {
			keys.add(p.getUniqueKey());
		}
		return keys;
	}
	
	private void checkValidity() {
		Set<String> uniqueKeys = new HashSet<String>();
		for(PredicateProfile profile : profiles) {
			String key = profile.getUniqueKey();
			Utils.checkTrue(!uniqueKeys.contains(key), "The key: " + key + " should not be contained.");
			uniqueKeys.add(key);
		}
		uniqueKeys.clear();
	}
}
