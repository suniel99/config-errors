package edu.washington.cs.conf.analysis.evol;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSACFG;
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
	
	public List<SSAInstruction> matchPredicateInNewCG(CGNode node, SSAInstruction ssa) {
		SSACFG cfg = node.getIR().getControlFlowGraph();
		//find the basic block containing the given ssa
		ISSABasicBlock bb = WALAUtils.getHostBasicBlock(node, ssa);
		Utils.checkNotNull(bb);
		
		//get succ
		List<ISSABasicBlock> succBBList = WALAUtils.getSuccBasicBlocks(node, bb);
		Utils.checkTrue(succBBList.size() == 2);
		
		//get the incoming basic block
		List<ISSABasicBlock> predBBList = WALAUtils.getPredBasicBlocks(node, bb);
		Utils.checkTrue(!predBBList.isEmpty());
		
		//look at the predicate in the new graph node
		CGNode newNode = this.getMatchedMethodInNewCG(node);
		
		//find the predicate in the new Node for which, the pred basic block
		//contains all previos instructions, and the succ basic blocks
		//contain all instructions.
		List<SSAInstruction> matchedInstructions = new LinkedList<SSAInstruction>();
		List<ISSABasicBlock> allBasicBlocks = WALAUtils.getAllBasicBlocks(newNode);
		for(ISSABasicBlock b : allBasicBlocks) {
			List<SSAInstruction> allInstr = WALAUtils.getAllIRs(b);
			if(allInstr.size() != 1) {
				continue;
			}
			if(!(allInstr.get(0) instanceof SSAConditionalBranchInstruction)) {
				continue;
			}
			//get the succ
			List<ISSABasicBlock> newSuccBlocks = WALAUtils.getSuccBasicBlocks(newNode, b);
			List<ISSABasicBlock> newPredBlocks = WALAUtils.getPredBasicBlocks(newNode, b);
			if(this.containBasicBlocks(succBBList, newSuccBlocks)
					&& this.containBasicBlocks(predBBList, newPredBlocks)) {
				matchedInstructions.add(allInstr.get(0));
			}
		}
		
		return matchedInstructions;
	}
	
	//FIXME wrong implementation
	private boolean containBasicBlocks(List<ISSABasicBlock> oldBBs, List<ISSABasicBlock> newBBs) {
		//every instruction in oldBBs is contained in newBBs
		
		for(ISSABasicBlock oldBB : oldBBs) {
			List<SSAInstruction> ssaList = WALAUtils.getAllIRs(oldBB);
			for(SSAInstruction ssa : ssaList) {
				boolean contained = false;
				for(ISSABasicBlock newBB : newBBs) {
					if(this.containInstruction(newBB, ssa)) {
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
	
	//FIXME approximate matching
	private boolean containInstruction(ISSABasicBlock bb, SSAInstruction ssa) {
		Class<?> ssaType = ssa.getClass();
		List<SSAInstruction> ssaList = WALAUtils.getAllIRs(bb);
		boolean hasSameType = false;
		for(SSAInstruction instruction : ssaList) {
			if(instruction != null && instruction.getClass().equals(ssaType)) {
				hasSameType = true;
			}
		}
		if(!hasSameType) {
			return false;
		}
		//further the method call
		boolean hasSameMethod = false;
		if(ssa instanceof SSAAbstractInvokeInstruction) {
			SSAAbstractInvokeInstruction invokeInstruction = (SSAAbstractInvokeInstruction)ssa;
			String methodName = invokeInstruction.getCallSite().getDeclaredTarget().getName().toString();
			for(SSAInstruction instruction : ssaList) {
				if(instruction instanceof SSAAbstractInvokeInstruction) {
					SSAAbstractInvokeInstruction innerInvoke = (SSAAbstractInvokeInstruction)instruction;
					String innverMethodName = innerInvoke.getCallSite().getDeclaredTarget().getName().toString();
					if(methodName.equals(innverMethodName)) {
						hasSameMethod = true;
						break;
					}
				}
			}
		}
		
		return hasSameMethod;
	}
	
	//FIXME just use the same method
	public CGNode getMatchedMethodInNewCG(CGNode node) {
		return WALAUtils.lookupMatchedCGNode(cgNew, node.getMethod().getSignature());
	}
}
