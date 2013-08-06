package edu.washington.cs.conf.analysis.evol.experiments;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestOptionsAndSlicing extends TestCase {

	public void testWekaOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldWekaPath);
		rep.showAll();
		//test thin slicing
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.oldWekaPath,
				CodeAnalyzerRepository.wekaMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
	}
	
	public void testWekaNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.wekaNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newWekaPath);
		rep.showAll();
		//test thin slicing
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.newWekaPath,
				CodeAnalyzerRepository.wekaMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			if(output.conf.getConfName().equals("m_numFolds")) {
				System.err.println(output.toString()); //XXX in
			}
		}
		
	}
	
	//some unreachable options are exluded in the file
	public void testRandoopOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop121Path);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.randoop121Path,
				CodeAnalyzerRepository.randoopMain, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
	}
	
	public void testRandoopNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.randoopNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.randoop132Path);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.randoop132Path,
				CodeAnalyzerRepository.randoopMain, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			if(output.conf.getConfName().equals("usethreads")) {
				System.err.println(output.toString());
				//contains the
			}
		}
	}
	
	public void testSynopticOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.oldSynopticPath);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.oldSynopticPath,
				CodeAnalyzerRepository.synopticMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
	}
	
	public void testSynopticNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.synopticNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.newSynopticPath);
		rep.showAll();
		
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(CodeAnalyzerRepository.newSynopticPath,
				CodeAnalyzerRepository.synopticMainClass, rep.getConfEntityList(), false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
			if(output.conf.getConfName().equals("dumpInitialGraphDotFile")) {
				System.err.println(output);
			}
			if(output.conf.getConfName().equals("dumpInitialGraphPngFile")) {
				System.err.println(output);
			}
		}
	}
	
	public void testJMeterOldOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterOldConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getOldJMeterPath());
		rep.showAll();
		
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		oldCoder.slicer.setContextSensitive(false);
		Collection<ConfPropOutput> outputs = CommonUtils.getConfPropOutputs(oldCoder.slicer, rep, false);
		for(ConfPropOutput output : outputs) {
			System.out.println(output.getConfEntity());
			System.out.println("   number of statements: " + output.statements.size());
		}
	}
	
	public void testJMeterNewOptions() {
		ConfEntityRepository rep = EvolConfOptionRepository.jmeterNewConfs();
		rep.initializeTypesInConfEntities(CodeAnalyzerRepository.getNewJMeterPath());
		rep.showAll();
	}
}