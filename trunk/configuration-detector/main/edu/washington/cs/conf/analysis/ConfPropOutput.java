package edu.washington.cs.conf.analysis;

import instrument.Globals;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.washington.cs.conf.util.Utils;

public class ConfPropOutput implements Serializable {
	private static final long serialVersionUID = 2670541082578233016L;
	
	public final ConfEntity conf; //fully serializable, no worry
	
	public final boolean ignoreLibs = true;
	                                                  
	
	//set is not serializable
	public final Set<IRStatement> statements;
	
	public ConfPropOutput(ConfEntity conf, Collection<IRStatement> stmts) {
		Utils.checkNotNull(conf);
		this.conf = conf;
		this.statements = new LinkedHashSet<IRStatement>();
		this.statements.addAll(stmts);
	}
	
	public ConfEntity getConfEntity() {
		return conf;
	}
	
	public Set<IRStatement> getNumberedStatements() {
		Set<IRStatement> numbered = new LinkedHashSet<IRStatement>();
		for(IRStatement ir : statements) {
			if(ir.hasLineNumber()) {
				numbered.add(ir);
			}
		}
		return numbered;
	}
	
	public Set<IRStatement> getNumberedBranches() {
		Set<IRStatement> branches = new LinkedHashSet<IRStatement>();
		for(IRStatement ir : statements) {
			if(ir.isBranch() && ir.hasLineNumber()) {
				branches.add(ir);
			}
		}
		return branches;
	}
	
	public Set<ShrikePoint> getNumberedBranchShrikePoints() {
		return this.getShrikePoints(this.getNumberedBranches());
	}
	
	//has a corresponding line number mapping to the source code
	public Set<ShrikePoint> getNumberedShrikePoints() {
		return this.getShrikePoints(this.getNumberedStatements());
	}
	
	public Set<ShrikePoint> getAllShrikePoints() {
		return this.getShrikePoints(this.statements);
	}
	
	private Set<ShrikePoint> getShrikePoints(Collection<IRStatement> stmts) {
		Set<ShrikePoint> pts = new LinkedHashSet<ShrikePoint>();
		for(IRStatement stmt : stmts) {
			if(ignoreLibs) {
				if(stmt.shouldIgnore()) {
				    continue;
				}
			}
			pts.add(new ShrikePoint(stmt));
		}
		return pts;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Seed:");
		sb.append(Globals.lineSep);
		sb.append(conf.toString());
		sb.append(Globals.lineSep);
		sb.append("Slicing results:");
		sb.append(Globals.lineSep);
		for(IRStatement s : statements) {
		    sb.append(s.toString());
		    sb.append(Globals.lineSep);
		}
		
		return sb.toString();
	}
}
