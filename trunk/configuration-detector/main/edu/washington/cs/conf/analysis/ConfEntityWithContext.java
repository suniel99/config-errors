package edu.washington.cs.conf.analysis;

import edu.washington.cs.conf.diagnosis.ConfEntityRepository;
import edu.washington.cs.conf.util.Utils;

public class ConfEntityWithContext {
	
	private final String configFullName;
	private final String context;

    public ConfEntityWithContext(String configFullName, String context) {
    	Utils.checkNotNull(configFullName);
    	Utils.checkNotNull(context);
    	this.configFullName = configFullName;
    	this.context = context;
    }
    
    public String getConfigFullName() {
    	return this.configFullName;
    }
    
    public String getContext() {
    	return this.context;
    }

    public ConfEntity getConfEntity(ConfEntityRepository repo) {
    	return repo.lookupConfEntity(this.configFullName);
    }
}
