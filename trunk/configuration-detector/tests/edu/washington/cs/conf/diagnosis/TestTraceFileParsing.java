package edu.washington.cs.conf.diagnosis;

import java.util.Collection;

import junit.framework.TestCase;

public class TestTraceFileParsing extends TestCase {
	
	public void testParseRandoopTraceFile() {
		String goodRandoopTrace = "./output/trace_dump_binarytree.txt";
		String badRandoopTrace = "./output/trace_dump_nanoxml.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodRandoopTrace, badRandoopTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
	}
	
	public void testParseRandoopTraceFile_context2() {
		String goodRandoopTrace = "./output/trace_dump_binarytree_2.txt";
		String badRandoopTrace = "./output/trace_dump_nanoxml_2.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodRandoopTrace, badRandoopTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
	}
	
	public void testParseWekaTraceFile() {
		String goodTrace = "./output/trace_dump_iris.txt";
		String badTrace = "./output/trace_dump_labor.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodTrace, badTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
		
	}
	
	public void testParseWekaTraceFile_context2() {
		String goodTrace = "./output/trace_dump_iris_2.txt";
		String badTrace = "./output/trace_dump_labor_2.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodTrace, badTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
		
	}
	
}