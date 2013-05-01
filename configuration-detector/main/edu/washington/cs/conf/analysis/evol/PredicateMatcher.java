package edu.washington.cs.conf.analysis.evol;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class PredicateMatcher {

	public final CallGraph cgOld;
	public final CallGraph cgNew;
	
	public PredicateMatcher(CallGraph cgOld, CallGraph cgNew) {
		Utils.checkNotNull(cgOld);
		Utils.checkNotNull(cgNew);
		this.cgOld = cgOld;
		this.cgNew = cgNew;
	}
	
	public SSAInstruction getPredicateInOldCG(String methodSig, int index) {
		SSAInstruction instruction = CodeAnalyzer.getInstruction(this.cgOld, methodSig, index);
		Utils.checkTrue(instruction instanceof SSAConditionalBranchInstruction);
		return instruction;
	}
	
	public CGNode getMethodInOldCG(String methodSig) {
		return WALAUtils.lookupMatchedCGNode(cgOld, methodSig);
	}
	
	public List<SSAInstruction> matchPredicateInNewCG(CGNode oldNode, SSAInstruction oldSsa) {
		//find the basic block containing the given ssa
		ISSABasicBlock bb = WALAUtils.getHostBasicBlock(oldNode, oldSsa);
		Utils.checkNotNull(bb);
		
		//get succ
		List<ISSABasicBlock> succBBList = WALAUtils.getSuccBasicBlocks(oldNode, bb);
		Utils.checkTrue(succBBList.size() == 2);
		System.out.println("succ bblist: " + succBBList);
		
		//get the incoming basic block
		List<ISSABasicBlock> predBBList = WALAUtils.getPredBasicBlocks(oldNode, bb);
		Utils.checkTrue(!predBBList.isEmpty());
		System.out.println("pred bblist: " + predBBList);
		
		//look at the predicate in the new graph node
		CGNode newNode = this.getMatchedMethodInNewCG(oldNode);
		Utils.checkNotNull(newNode);
		
		//find the predicate in the new Node for which, the pred basic block
		//contains all previos instructions, and the succ basic blocks
		//contain all instructions.
		List<SSAInstruction> matchedInstructions = new LinkedList<SSAInstruction>();
		List<ISSABasicBlock> allBasicBlocks = WALAUtils.getAllBasicBlocks(newNode);
		for(ISSABasicBlock b : allBasicBlocks) {
			List<SSAInstruction> allInstr = WALAUtils.getAllIRs(b);
			//exclude the entry / exit block
			if(allInstr.isEmpty()) {
				continue;
			}
			//the last one should be Branch Instruction
			if(!(allInstr.get(allInstr.size() - 1) instanceof SSAConditionalBranchInstruction)) {
				continue;
			}
//			System.out.println("check: " + allInstr);
			//get the succ
			List<ISSABasicBlock> newSuccBlocks = WALAUtils.getSuccBasicBlocks(newNode, b);
			List<ISSABasicBlock> newPredBlocks = WALAUtils.getPredBasicBlocks(newNode, b);
			if(this.containBasicBlocks(succBBList, newSuccBlocks)
					&& this.containBasicBlocks(predBBList, newPredBlocks)) {
				//for debugging purpose:
				//its containing basic block
				System.out.println("Hosting basic block: ");
				System.out.println(allInstr);
				matchedInstructions.add(allInstr.get(allInstr.size() - 1));
			}
		}
		
		return matchedInstructions;
	}
	
	//FIXME wrong implementation
	private boolean containBasicBlocks(List<ISSABasicBlock> oldBBs, List<ISSABasicBlock> newBBs) {
		//the number of new basic block must >=  the number of new basic block 
		if(newBBs.size() < oldBBs.size()) {
			return false;
		}
		
		//every instruction in oldBBs is contained in newBBs		
		for(ISSABasicBlock oldBB : oldBBs) {
			List<SSAInstruction> ssaList = WALAUtils.getAllIRs(oldBB);
			for(SSAInstruction ssa : ssaList) {
				boolean contained = false;
				for(ISSABasicBlock newBB : newBBs) {
					if(CodeAnalyzer.containInstruction(newBB, ssa)) {
						contained = true;
						break;
					}
				}
				if(!contained) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	//FIXME just use method with the same name
	public CGNode getMatchedMethodInNewCG(CGNode node) {
		return WALAUtils.lookupMatchedCGNode(cgNew, node.getMethod().getSignature());
	}
}