package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.util.Utils;

public abstract class AbstractBaselineDiagnoser {
    public final Collection<ConfPropOutput> confs;
	
	public final Collection<StmtExecuted> stmts;
	
	public final Map<String, Float> sigMap;
	
	public AbstractBaselineDiagnoser(Collection<ConfPropOutput> confs,
			Collection<StmtExecuted> stmts, Map<String, Float> sigMap) {
		this.confs = confs;
		this.stmts = stmts;
		this.sigMap = Utils.sortByValue(sigMap, false);
	}
	
	public abstract List<ConfEntity> computeResponsibleOptions();
}
