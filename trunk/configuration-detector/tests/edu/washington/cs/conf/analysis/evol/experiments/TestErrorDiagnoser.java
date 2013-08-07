package edu.washington.cs.conf.analysis.evol.experiments;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.analysis.evol.ErrorDiagnoser;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.analysis.evol.TraceRepository;
import edu.washington.cs.conf.analysis.evol.TracesWrapper;
import junit.framework.TestCase;

public class TestErrorDiagnoser extends TestCase {
	
	public void testRandoop() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.randoopOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.randoopNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getRandoop132Analyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getRandoopTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
		diagnoser.diagnoseRootCauses();
	}
	
	public void testWeka() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.wekaOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.wekaNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getWekaOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getWekaNewAnalyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getWekaTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
		diagnoser.diagnoseRootCauses();
	}
	
	public void testSynoptic() {
		ConfEntityRepository oldConf = EvolConfOptionRepository.synopticOldConfs();
		ConfEntityRepository newConf = EvolConfOptionRepository.synopticNewConfs();
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		newCoder.buildAnalysis();
		TracesWrapper wrapper = TraceRepository.getSynopticTraces();
		
		ErrorDiagnoser diagnoser = new ErrorDiagnoser(oldConf, newConf, oldCoder, newCoder, wrapper);
		diagnoser.diagnoseRootCauses();
	}

}
