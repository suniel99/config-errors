package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;

/**
 * This class is for experimental comparison with method-level invariant
 * analysis. The basic idea is: observe the invariant difference between
 * a good run and a bad run, and find out all methods that have different
 * invariants. Then, identify configurations that could affect such methods.
 * 
 * Compared to the configuration profiling, this method is at a coarser
 * granularity.
 * */
public class MethodBasedDiagnoser extends AbstractBaselineDiagnoser {

	public MethodBasedDiagnoser(Collection<ConfPropOutput> confs,
			Map<String, Float> sigMap) {
		super(confs, sigMap);
	}

	@Override
	public List<ConfEntity> computeResponsibleOptions() {
		List<ConfEntity> entityList = new LinkedList<ConfEntity>();
		
		for(String methodSig : this.sigMap.keySet()) {
			List<ConfEntity> confs = this.findConfEntities(methodSig, this.confs);
			entityList.addAll(confs);
		}
		
		return entityList;
	}
	
	private List<ConfEntity> findConfEntities(String methodSig,
			Collection<ConfPropOutput> confs) {
		List<ConfEntity> matched = new LinkedList<ConfEntity>();
		
		for(ConfPropOutput conf : confs) {
			if(conf.findStatementByMethod(methodSig)) {
				matched.add(conf.getConfEntity());
			}
		}
		
		return matched;
	}
	
	/**
	 * integrate with Daikon
	 * */
	public static List<ConfEntity> computeResponsibleOptions(Collection<String> goodInvFiles, String goodInvFile,
			Collection<ConfPropOutput> confs) {
		//get method scores
		InvariantDiffAnalyzer analyzer = new InvariantDiffAnalyzer(goodInvFiles, goodInvFile);
		Map<String, Float> scores = analyzer.getMethodsWithDiffInvariants();
		//find responsbile options
		MethodBasedDiagnoser diagnoser = new MethodBasedDiagnoser(confs, scores);
		List<ConfEntity> entityList = diagnoser.computeResponsibleOptions();
		return entityList;
	}
}