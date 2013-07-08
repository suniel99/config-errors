package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.PhiStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractThrowInstruction;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAPhiInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class SliceSeedFinder {

	/**
	 * Given a predicate in a node, find all possible seeds.
	 * */
	//for each variable, check whether it escapes the block
	public Collection<Statement> createSliceSeeds(CGNode node, int predIndex,
			Set<ISSABasicBlock> executedBlocks) {
		SSAInstruction predSSA = node.getIR().getInstructions()[predIndex];
		Utils.checkTrue(CodeAnalysisUtils.isPredicateInstruction(predSSA));
		//get every instructions in the branch
		
		ISSABasicBlock predBlock = node.getIR().getBasicBlockForInstruction(predSSA);
		
		ISSABasicBlock postDomBlock = PostDominatorFinder.computeImmediatePostDominator(node, predBlock);
		
		SSAInstruction postDomInstr = PostDominatorFinder.getImmediatePostDominatorInstruction(node, postDomBlock);
		
		System.out.println(postDomBlock);
//		WALAUtils.printBasicBlock(postDomBlock);
		System.out.println(postDomInstr);
		System.out.println("Index: " + WALAUtils.getInstructionIndex(node, postDomInstr));
		
		//only care about basic blocks between the starting block and the ending block
		//first all basic blocks between them
	    
		ISSABasicBlock startBlock = predBlock;
		ISSABasicBlock endBlock = postDomBlock;
		
		Set<ISSABasicBlock> betweenBlocks = PostDominatorFinder.findAllBasicBlocksBetween(node, startBlock);
		System.out.println(WALAUtils.getAllBasicBlockIDList(betweenBlocks));
		
		//NOT enabled yet FIXME
		//Utils.intersect(betweenBlocks, executedBlocks);
		Set<ISSABasicBlock> intersectBlocks = betweenBlocks;
		intersectBlocks.remove(startBlock);
		intersectBlocks.remove(endBlock);
		
		//find out instructions that is not in other predicate
		Set<ISSABasicBlock> predicateBlocks = new LinkedHashSet<ISSABasicBlock>();
		for(ISSABasicBlock bb : intersectBlocks) {
			if(bb == startBlock || bb == endBlock) {
				continue;
			}
			//skip empty block
			if(!bb.iterator().hasNext()) {
				continue;
			}
			if(CodeAnalysisUtils.isPredicateInstruction(bb.getLastInstruction())) {
				predicateBlocks.add(bb);
			}
		}
		
		for(ISSABasicBlock bb : predicateBlocks) {
			Set<ISSABasicBlock> bbSet = PostDominatorFinder.findAllBasicBlocksBetween(node, bb);
			intersectBlocks.removeAll(bbSet);
		}
		
		System.out.println("After removing nested");
		System.out.println(WALAUtils.getAllBasicBlockIDList(intersectBlocks));
		
		//find all instructions
		Set<SSAInstruction> instrSet = new LinkedHashSet<SSAInstruction>();
		for(ISSABasicBlock bb : intersectBlocks) {
			Iterator<SSAInstruction> iter = bb.iterator();
			while(iter.hasNext()) {
				SSAInstruction instr = iter.next();
				if(valueMayFlowOut(node, instr, instrSet)) {
					instrSet.add(instr);
				}
			}
		}
		
		//find out instructions whose value may "flow out"
		
		//for every thin slicing, reduce the weight half, if the slice result
		//does not affect the predicate, ignore it.
		
		Collection<Statement> stmts = new LinkedHashSet<Statement>();
		
		for(SSAInstruction ssa : instrSet) {
			int index = WALAUtils.getInstructionIndex(node, ssa);
			if(index != -1) {
				Statement s = new NormalStatement(node, index);
				stmts.add(s);
				System.out.println(" index: " + index + ", bb id: " + node.getIR().getBasicBlockForInstruction(ssa));
			}
		}
		
		//we also need to return the phi statements in the end block
		Iterator<SSAPhiInstruction> iter = endBlock.iteratePhis();
		while(iter.hasNext()) {
			SSAPhiInstruction ssaPhiInstr = iter.next();
			PhiStatement phiStmt = new PhiStatement(node, ssaPhiInstr);
			stmts.add(phiStmt);
		}
		
		
		return stmts;
		
	}
	
	//FIXME
	public boolean valueMayFlowOut(CGNode node, SSAInstruction ssa, Set<SSAInstruction> set) {
		//test whether the
		if(ssa instanceof SSAPutInstruction) {
			return true;
		}
		if(ssa instanceof SSAGetInstruction) {
			return false;
		}
		if(ssa instanceof SSAAbstractThrowInstruction) {
			return false;
		}
		if(!ssa.hasDef()) {
			return false;
		}
		return true;
	}
	
}