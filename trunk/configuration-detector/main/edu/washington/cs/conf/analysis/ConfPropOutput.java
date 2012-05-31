package edu.washington.cs.conf.analysis;

import instrument.Globals;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

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
	
	public Set<IRStatement> getNumberedBranchesInSource() {
		Set<IRStatement> branches = new LinkedHashSet<IRStatement>();
		for(IRStatement ir : statements) {
			if(ir.hasLineNumber() && ir.isBranchInSource()) {
				branches.add(ir);
			}
		}
		return branches;
	}
	
	public Set<ShrikePoint> getNumberedBranchShrikePoints() {
		return this.getShrikePoints(this.getNumberedBranches());
	}
	
	public Set<ShrikePoint> getNumberedBranchShrikePointsInSource() {
		//return this.getShrikePoints(this.getNumberedBranchesInSource());
		Set<IRStatement> irs = new LinkedHashSet<IRStatement>();
		
		for(IRStatement ir : this.statements) {
			if(!ir.hasLineNumber()) {
				continue;
			}
			if(ir.isBranch()) {
				irs.add(ir);
			} else {
				List<IRStatement> mappedIRs = this.getMappedBranchInSource(ir);
				if(!mappedIRs.isEmpty()) {
				    irs.addAll(mappedIRs);
				}
			}
		}
		Set<ShrikePoint> retPoints = this.getShrikePoints(irs);
		
		return retPoints;
	}
	
	private List<IRStatement> getMappedBranchInSource(IRStatement ir) {
		CGNode node = ir.getStatement().getNode();
		int lineNum = ir.getLineNumber();
		Utils.checkTrue(lineNum != -1);
		List<IRStatement> matchedStmts = new LinkedList<IRStatement>();
		for(int index = 0; index < node.getIR().getInstructions().length; index++) {
			SSAInstruction ssa = node.getIR().getInstructions()[index];
			if(ssa instanceof SSAConditionalBranchInstruction) {
				NormalStatement stmt = new NormalStatement(node, index);
				int sNum = WALAUtils.getStatementLineNumber(stmt);
				if(sNum == lineNum) {
					IRStatement irs = new IRStatement(stmt);
					matchedStmts.add(irs);
				}
			}
		}
		
		return matchedStmts;
		
		//FIXME prune out the StringBuilder, example in RelMclsValAsgnInst, visitMoveInst
		
//		if(matchedStmts.size() > 1) {
//			System.err.println("In node: " + node);
//			System.err.println("ir: " + ir);
//			System.err.println("matched stmts: " + matchedStmts);
//			throw new Error("ir: " + ir + ", matched stmts: " + matchedStmts);
//		}
//		
//		if(matchedStmts.isEmpty()) {
//			return null;
//		} else {
//			return matchedStmts.get(0);
//		}
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
