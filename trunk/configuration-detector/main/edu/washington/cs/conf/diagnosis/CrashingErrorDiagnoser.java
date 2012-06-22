package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

/**
 * Implements a diagnoser for configuration crashing errors.
 * 
 * Diagnosing crashing errors is significantly different than diagnosing non-crashing ones.
 * 
 * 1. it is unlikely to exist a similar trace in the database for the crashing runs
 * 2. it does not need to compare the whole trace to identify the different parts, since
 *    the crashing trace is an incomplete profile over the whole execution.
 *    
 * Methodology:
 * - only compare the profiles in the stack trace?
 * - only compare the common predicates in the crashing trace
 * - no filtering, the error can be just off-by-one
 * */
public class CrashingErrorDiagnoser {
	
    public final Collection<PredicateProfileTuple> goodRuns;
	
	public final PredicateProfileTuple badRun;
	
	public final ConfEntityRepository repository;
	
	private String[] stackTraces = null;
	
	public CrashingErrorDiagnoser(Collection<PredicateProfileTuple> goodRuns,
			PredicateProfileTuple badRun, ConfEntityRepository repository) {
		Utils.checkNotNull(goodRuns);
		Utils.checkNotNull(badRun);
		Utils.checkNotNull(repository);
		this.goodRuns = goodRuns;
		this.badRun = badRun;
		this.repository = repository;
	}
	
	public void setStackTraces(String file) {
		String[] traces = Files.readWholeNoExp(file).toArray(new String[0]);
		this.setStackTraces(traces);
	}
	
	public void setStackTraces(String[] stackTraces) {
		Utils.checkNotNull(stackTraces);
		this.stackTraces = new String[stackTraces.length];
		for(int i = 0; i < stackTraces.length; i++) {
			this.stackTraces[i] = stackTraces[i];
		}
	}
	
	//only the executed parts in the crashing trace
	public List<ConfDiagnosisOutput> computeResponsibleOptionsInCrashingTrace() {
		
		//remove some parts in the good trace
		List<PredicateProfileTuple> filteredGoodRuns = new LinkedList<PredicateProfileTuple>();
		for(PredicateProfileTuple goodRun : this.goodRuns) {
			Collection<PredicateProfile> filteredProfiles = new LinkedList<PredicateProfile>();
			Collection<PredicateProfile> profiles = goodRun.getAllProfiles();
			for(PredicateProfile p : profiles) {
				if(this.badRun.lookUpByUniqueKey(p.getUniqueKey()) != null) {
					filteredProfiles.add(p);
				}
			}
			//create the good profile tuple
			PredicateProfileTuple filteredTuple = PredicateProfileTuple.createGoodRun(goodRun.name, filteredProfiles);
			filteredGoodRuns.add(filteredTuple);
		}
		
		PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(filteredGoodRuns, this.badRun, this.repository);
		
        return diagnoser.computeResponsibleOptions();
	}
	
	//only the executed parts in the stack trace
	public List<ConfDiagnosisOutput> computeResponsibleOptionsWithStackTrace() {
		Utils.checkNotNull(this.stackTraces);
		String[] methods = this.fetchMethodFromStackTrace(this.stackTraces);
		
		//recreate the bad profiles
		Collection<PredicateProfile> filteredBadProfiles = new LinkedList<PredicateProfile>();
		for(PredicateProfile p : this.badRun.getAllProfiles()) {
			if(Utils.startWith(p.getContext(), methods)) {
				filteredBadProfiles.add(p);
			}
		}
		PredicateProfileTuple filteredBadTuple = PredicateProfileTuple.createBadRun(this.badRun.name, filteredBadProfiles);
		
		//recreate the good profiles
		Collection<PredicateProfileTuple> filteredGoodTuples = new LinkedList<PredicateProfileTuple>();
		for(PredicateProfileTuple goodTuple : this.goodRuns) {
			Collection<PredicateProfile> filteredGoodProfiles = new LinkedList<PredicateProfile>();
			for(PredicateProfile p : goodTuple.getAllProfiles()) {
				if(Utils.startWith(p.getContext(), methods)) {
					filteredGoodProfiles.add(p);
				}
			}
			PredicateProfileTuple filteredGoodTuple = PredicateProfileTuple.createGoodRun(goodTuple.name, filteredGoodProfiles);
			filteredGoodTuples.add(filteredGoodTuple);
		}
		
		//start diagnosis
		PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(filteredGoodTuples, filteredBadTuple, this.repository);
	
        return diagnoser.computeResponsibleOptions();
	}
	
	//a single stack trace looks like: at chord.project.Main.main(Main.java: 19)
	private String[] fetchMethodFromStackTrace(String[] traces) {
		String at = "at ";
		String[] methods = new String[traces.length];
		int count = 0;
		for(String trace : traces) {
			trace = trace.trim();
			int startIndex = trace.indexOf(at);
			int endIndex = trace.indexOf("(");
			Utils.checkTrue(startIndex != -1 && endIndex != -1 && endIndex > startIndex, trace);
			String method = trace.substring(startIndex + at.length(), endIndex);
			methods[count++] = method;
//			System.out.println(method);
		}
		return methods;
	}
	
	//diagnose like diagnosing non-crashing errors
	public List<ConfDiagnosisOutput> computeResponsibleOptionsAsNonCrashingErrors() {
        PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(this.goodRuns, this.badRun, this.repository);
        return diagnoser.computeResponsibleOptions();
	}
}