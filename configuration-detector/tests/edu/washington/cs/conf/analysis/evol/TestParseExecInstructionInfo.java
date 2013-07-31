package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;

import junit.framework.TestCase;

public class TestParseExecInstructionInfo extends TestCase {

	public void testParseRandoop() {
		Collection<PredicateExecInfo>  oldPredExecs
           = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.randoopOldPredicateDump,
        		TraceRepository.randoopOldSig);
	    Collection<PredicateExecInfo> newPredExecs
	       = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.randoopNewPredicateDump,
	    		TraceRepository.randoopNewSig);
	    Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
//	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
//	    System.out.println(newUnmatchedMethods);
	}
	
	public void testParseSynoptic() {
		Collection<PredicateExecInfo>  oldPredExecs
	        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.synopticOldPredicateDump,
	        		TraceRepository.synopticOldSig);
		Collection<PredicateExecInfo> newPredExecs
		    = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.synopticNewPredicateDump,
		    		TraceRepository.synopticNewSig);
		Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
		System.out.println(oldUnmatchedMethods);
		System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
		System.out.println(newUnmatchedMethods);
	}
	
	public void testParseWeka() {
		Collection<PredicateExecInfo>  oldPredExecs
          = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.wekaOldPredicateDump,
		    TraceRepository.wekaOldSig);
		System.out.print("read old predicates: " + oldPredExecs.size());
        Collection<PredicateExecInfo> newPredExecs
          = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.wekaNewPredicateDump,
		    TraceRepository.wekaNewSig);
        Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
        Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
        System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
        System.out.println(oldUnmatchedMethods);
        System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
        System.out.println(newUnmatchedMethods);
	}
	
	//need to analyze different behaviors of the predicate
	public void testParseJMeter() {
		Collection<PredicateExecInfo>  oldPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.jmeterOldPredicateDump,
    		    TraceRepository.jmeterOldSig);
        Collection<PredicateExecInfo> newPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.jmeterNewPredicateDump,
    		    TraceRepository.jmeterNewSig);
        Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
        System.out.println(newUnmatchedMethods);
	}
}