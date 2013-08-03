package edu.washington.cs.conf.analysis.evol.experiments;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import junit.framework.TestCase;

public class TestOptionsAndSlicing extends TestCase {

	public void testWekaOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldWekaPath);
		rep.showAll();
	}
	
	public void testWekaNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newWekaPath);
		rep.showAll();
	}
	
	public void testRandoopOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop121Path);
		rep.showAll();
	}
	
	public void testRandoopNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop132Path);
		rep.showAll();
	}
	
	public void testSynopticOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldSynopticPath);
		rep.showAll();
	}
	
	public void testSynopticNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newSynopticPath);
		rep.showAll();
	}
	
	//XXX
	public void testJMeterOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getOldJMeterPath());
		rep.showAll();
	}
	
	//XXX
	public void testJMeterNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getNewJMeterPath());
		rep.showAll();
	}
}