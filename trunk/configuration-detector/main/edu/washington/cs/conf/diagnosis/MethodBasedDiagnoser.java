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
			if(conf.containStatement(methodSig)) {
				matched.add(conf.getConfEntity());
			}
		}
		
		return matched;
	}
}
