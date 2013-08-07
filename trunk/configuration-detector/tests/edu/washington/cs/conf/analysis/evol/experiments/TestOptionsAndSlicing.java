package edu.washington.cs.conf.analysis.evol.experiments;

import java.util.Collection;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ShrikePoint;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
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
		
		String saveFileName = EvolConfOptionRepository.wekaOldCacheFile;
		this.saveAndCheckSlicingResult(saveFileName, outputs);
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
		
		String saveFileName = EvolConfOptionRepository.wekaNewCacheFile;
		this.saveAndCheckSlicingResult(saveFileName, outputs);
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
		
		String saveFileName = EvolConfOptionRepository.randoopOldCacheFile;
		this.saveAndCheckSlicingResult(saveFileName, outputs);
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
		
		String saveFileName = EvolConfOptionRepository.randoopNewCacheFile;
		this.saveAndCheckSlicingResult(saveFileName, outputs);
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
		
		String saveFileName = EvolConfOptionRepository.synopticOldCacheFile;
		this.saveAndCheckSlicingResult(saveFileName, outputs);
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
		
		String saveFileName = EvolConfOptionRepository.synopticNewCacheFile;
		this.saveAndCheckSlicingResult(saveFileName, outputs);
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
	private void saveAndCheckSlicingResult(String saveFileName, Collection<ConfPropOutput> outputs) {
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.ALL_PRED_STMT);
		schema.addInstrumentationPoint(outputs);
		System.out.println("Before serializing, check outputs inside: ");
		System.out.println("Map size: " + schema.getLocations().size());
		for(ConfEntity e : schema.getLocations().keySet()) {
			System.out.println(e + ", number of statements: " + schema.getLocations().get(e).size());
		}
		ConfOutputSerializer.serializeSchema(schema, saveFileName);
		
		System.out.println("Read it back..");
		
		schema = ConfOutputSerializer.deserializeAsSchema(saveFileName);
		Map<ConfEntity, Collection<ShrikePoint>> locations = schema.getLocations();
		for(ConfEntity e : locations.keySet()) {
			System.out.println(e + ", number of statements: " + locations.get(e).size());
//			for(ShrikePoint p : locations.get(e)) {
//				System.out.println("   " + p);
//			}
		}
	}
}