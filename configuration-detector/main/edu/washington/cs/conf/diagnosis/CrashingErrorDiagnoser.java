package edu.washington.cs.conf.diagnosis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.slicer.Statement;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Log;
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
			//print the debugging information
			System.out.println(" - number of profiles in the good run before filtering: " + goodRun.getAllProfiles().size());
			System.out.println(" - number of profiles in the good run after filtering: " + filteredTuple.getAllProfiles().size());
			System.out.println();
		}
		
		PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(filteredGoodRuns, this.badRun, this.repository);
		
		System.out.println(" > number of profiles in the bad run: " + this.badRun.getAllProfiles().size());
		System.out.println();
		
        return diagnoser.computeResponsibleOptions();
	}
	
	//only the executed parts in the stack trace
	public List<ConfDiagnosisOutput> computeResponsibleOptionsWithStackTrace() {
		return computeResponsibleOptionsWithStackTrace(RankType.SINGLE_IMPORT);
	}
	public List<ConfDiagnosisOutput> computeResponsibleOptionsWithStackTrace(RankType type) {
		System.out.println("Compute responsible options only covered by the stack trace... ");
		Utils.checkNotNull(this.stackTraces);
		String[] methods = fetchMethodFromStackTrace(this.stackTraces);
		
		//recreate the bad profiles
		Collection<PredicateProfile> filteredBadProfiles = new LinkedList<PredicateProfile>();
		for(PredicateProfile p : this.badRun.getAllProfiles()) {
//			System.out.println(p.getContext());
			if(Utils.startWith(p.getContext(), methods)) {
				//System.out.println("add");
				filteredBadProfiles.add(p);
			}
		}
		PredicateProfileTuple filteredBadTuple = PredicateProfileTuple.createBadRun(this.badRun.name, filteredBadProfiles);
		System.out.println(" > number of profiles in the bad run before filtering: " + this.badRun.getAllProfiles().size());
		System.out.println(" > number of profiles in the bad run after filtering: " + filteredBadTuple.getAllProfiles().size());
		System.out.println();
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
			//see the filtered num
			System.out.println(" - number of profiles in the good run before filtering: " + goodTuple.getAllProfiles().size());
			System.out.println(" - number of profiles in the good run before filtering: " + filteredGoodTuple.getAllProfiles().size());
		}
		System.out.println();
		
		//start diagnosis
		PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(filteredGoodTuples, filteredBadTuple, this.repository);
	
        return diagnoser.computeResponsibleOptions(type);
	}
	
	//a single stack trace looks like: at chord.project.Main.main(Main.java: 19)
	private static String[] fetchMethodFromStackTrace(String[] traces) {
		String[] methods = new String[traces.length];
		int count = 0;
		for(String trace : traces) {
			methods[count++] = fetchMethodFromStackTrace(trace);
//			System.out.println(method);
		}
		return methods;
	}
	
	private static String fetchMethodFromStackTrace(String trace) {
		String at = "at ";
		trace = trace.trim();
		int startIndex = trace.indexOf(at);
		int endIndex = trace.indexOf("(");
		Utils.checkTrue(startIndex != -1 && endIndex != -1 && endIndex > startIndex, trace);
		String method = trace.substring(startIndex + at.length(), endIndex);
		method = method.trim();
		return method;
	}
	
	private static int fetchLineNumberFromStackTrace(String trace) {
		trace = trace.trim();
		int startIndex = trace.lastIndexOf(":");
		int endIndex = trace.lastIndexOf(")");
		if(startIndex == -1 || endIndex == -1) {
			return -1;
		}
		Utils.checkTrue(endIndex > startIndex);
		String str = trace.substring(startIndex + 1, endIndex).trim();
		return Integer.parseInt(str);
	}
	
	//diagnose like diagnosing non-crashing errors
	public List<ConfDiagnosisOutput> computeResponsibleOptionsAsNonCrashingErrors() {
        PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(this.goodRuns, this.badRun, this.repository);
        return diagnoser.computeResponsibleOptions();
	}
	
	public List<ConfDiagnosisOutput> computeResponsibleOptionsAsNonCrashingErrors(RankType type) {
        PredicateProfileBasedDiagnoser diagnoser
            = new PredicateProfileBasedDiagnoser(this.goodRuns, this.badRun, this.repository);
        return diagnoser.computeResponsibleOptions(type);
	}
	
	public static List<ConfDiagnosisOutput> rankConfigurationOptions(Map<ConfDiagnosisOutput, Integer> stackCoverage, Collection<ConfDiagnosisOutput> rankedList) {
		Map<ConfDiagnosisOutput, Float> scores = new LinkedHashMap<ConfDiagnosisOutput, Float>();
		
		for(ConfDiagnosisOutput o : rankedList) {
			scores.put(o, o.getFinalScore() + stackCoverage.get(o));
		}
		
		scores = Utils.sortByValue(scores, false);
		System.out.println("-----------------intermediate results----------------");
		for(ConfDiagnosisOutput o : scores.keySet()) {
			System.out.println(o.getConfEntity().getFullConfName() + ",   " + scores.get(o));
		}
		List<ConfDiagnosisOutput> finalRankedList = Utils.sortByValueAndReturnKeys(scores, false);
		
		return finalRankedList;
	}
	
	//check when the configuration can affect the line number in the stack trace
	public static Map<ConfDiagnosisOutput, Integer> computeMatchedStacktraceNum(Collection<ConfPropOutput> confSlices,
			Collection<ConfDiagnosisOutput> outputs, String[] stackTraces) {
		List<String> methods = new ArrayList<String>();
		List<Integer> lines = new ArrayList<Integer>();
		for(String trace : stackTraces) {
			String method = fetchMethodFromStackTrace(trace);
			Integer lineNum = fetchLineNumberFromStackTrace(trace);
			if(lineNum == -1) {
				System.err.println("no line number: " + trace);
				continue;
			}
			methods.add(method);
			lines.add(lineNum);
		}
		Utils.checkTrue(methods.size() == lines.size());
		//then rank each ConfDiagnosisOutput based on the match
		Map<ConfDiagnosisOutput, Integer> map = new LinkedHashMap<ConfDiagnosisOutput, Integer>();
		for(ConfDiagnosisOutput output : outputs) {
			//first get the ConfPropOutput
			ConfPropOutput confSlice = findConfDiagnosisOutput(confSlices, output); 
			Utils.checkNotNull(confSlice);
			//count the num
			Integer matchedStackTraceNum = 0;
			for(int i = 0; i < methods.size(); i++) {
				String method = methods.get(i);
				int lineNum = lines.get(i);
				if(confSlice.includeStatement(method, lineNum)) {
					matchedStackTraceNum ++;
//					matchedStackTraceNum += (methods.size() - i + 1);
				}
			}
			//put to the map
			map.put(output, matchedStackTraceNum);
		}
		
		return map;
	}
	
	public static Map<String, Integer> computeStackTraceDistance(Collection<ConfPropOutput> confSlices, ConfDiagnosisOutput output, String[] stackTraces) {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		//in the output slice, the distance between the stack trace methods to the configuration entry
		for(String stackTrace : stackTraces) {
			String method = fetchMethodFromStackTrace(stackTrace);
			Integer lineNum = fetchLineNumberFromStackTrace(stackTrace);
			if(lineNum == -1) {
				continue;
			}
			ConfPropOutput confSlice = findConfDiagnosisOutput(confSlices, output); 
			Utils.checkNotNull(confSlice);
			
			Set<IRStatement> filtered = ConfPropOutput.excludeIgnorableStatements(confSlice.statements);
			Set<IRStatement> statements = ConfUtils.removeSameStmtsInDiffContexts(filtered);// filterSameStatements(filtered);
			
			//compute the distance
			IRStatement stmt = null;
			for(IRStatement irs : statements) {
				if(irs.getMethodSig().startsWith(method) && irs.getLineNumber() == lineNum) {
					stmt = irs;
					break;
				}
			}
			int distance = Integer.MAX_VALUE;
			if(stmt != null) {
				if(confSlice.getConfigurationSlicer() != null) {
				    Statement seed = confSlice.getConfigurationSlicer().extractConfStatement(confSlice.getConfEntity());
				    Statement target = stmt.getStatement();
				    distance = confSlice.getConfigurationSlicer().computeDistanceInThinSlicing(seed, target);
				}
			}
			
			map.put(stackTrace, distance);
		}
		return map;
	}
	
	private static ConfPropOutput findConfDiagnosisOutput(Collection<ConfPropOutput> confSlices, ConfDiagnosisOutput output) {
		ConfPropOutput confSlice = null;
		for(ConfPropOutput slice : confSlices) {
			if(slice.getConfEntity().getFullConfName().equals(output.getConfEntity().getFullConfName())) {
				confSlice = slice;
				break;
			}
		}
		return confSlice;
//		Utils.checkNotNull(confSlice);
		
	}
}