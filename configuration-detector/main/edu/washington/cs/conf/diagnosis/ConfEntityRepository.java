package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashSet;

import edu.washington.cs.conf.analysis.ConfEntity;

public class ConfEntityRepository {

	public final Collection<ConfEntity> entities = new LinkedHashSet<ConfEntity>();
	
	public ConfEntityRepository(Collection<ConfEntity> entities) {
		this.entities.addAll(entities);
		if(this.entities.size() != entities.size()) {
			System.err.println("Warning, size not equal. Given: "
					+ entities.size() + ", but result in: " + this.entities.size());
		}
	}
	
	public ConfEntity lookupConfEntity(String fullName) {
		for(ConfEntity entity : entities) {
			if(entity.getFullConfName().equals(fullName)) {
				return entity;
			}
		}
		return null;
	}
	
}
