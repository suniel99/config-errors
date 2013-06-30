package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class InstructionExecChuck {

	public final List<InstructionExecInfo> instructions;
	
	public InstructionExecChuck(List<InstructionExecInfo> instructions) {
		Utils.checkNotNull(instructions);
		this.instructions = instructions;
	}
	
	public List<InstructionExecInfo> getInstructions() {
		return this.instructions;
	}
	
	public int size() {
		return this.instructions.size();
	}
	
	public boolean containInstruction(CGNode node, int index) {
		return !this.getInstructions(node, index).isEmpty();
	}
	
	public List<SSAInstruction> getInstructions(CGNode node, int index) {
		String methodSig = node.getMethod().getSignature();
		List<SSAInstruction> ssas = new LinkedList<SSAInstruction>();
		for(InstructionExecInfo ssaExec : this.instructions) {
			if(ssaExec.getMethodSig().equals(methodSig) && index == ssaExec.index) {
				SSAInstruction ssa =  WALAUtils.getInstruction(node, index);
				ssas.add(ssa);
			}
		}
		return ssas;
	}
	
	public List<SSAInstruction> getInstructions(CGNode node, int startIndex, int endIndex) {
		throw new Error();
	}
	
	public boolean evaluateToTrue(CGNode node, int index) {
		SSAInstruction ssa = WALAUtils.getInstruction(node, index);
		Utils.checkTrue(CodeAnalysisUtils.isPredicateInstruction(ssa));
		
		throw new Error("Not implemented");
		
	}
}