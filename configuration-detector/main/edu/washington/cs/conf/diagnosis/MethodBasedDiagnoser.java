package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;

public class MethodBasedDiagnoser extends AbstractBaselineDiagnoser {

	public MethodBasedDiagnoser(Collection<ConfPropOutput> confs,
			Collection<StmtExecuted> stmts, Map<String, Float> sigMap) {
		super(confs, stmts, sigMap);
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
