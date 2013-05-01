package edu.washington.cs.conf.analysis.evol;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import plume.Pair;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class MethodMatcher {
	
	public final CallGraph cgOld;
	public final CallGraph cgNew;
	
	public MethodMatcher(CallGraph cgOld, CallGraph cgNew) {
		Utils.checkNotNull(cgOld);
		Utils.checkNotNull(cgNew);
		this.cgOld = cgOld;
		this.cgNew = cgNew;
	}
	
	public CGNode getMethodInOldCG(String methodSig) {
		return WALAUtils.lookupMatchedCGNode(cgOld, methodSig);
	}
	
	public List<CGNode> getMethodInNewCG(CGNode oldNode) {
		List<CGNode> retNodes = new LinkedList<CGNode>();
		Utils.checkNotNull(oldNode);
		//first check the full qualified names
		CGNode newNode = WALAUtils.lookupMatchedCGNode(this.cgNew, oldNode.getMethod().getSignature());
		if(newNode != null) {
			System.out.println("Use the exact matching rule.");
			retNodes.add(newNode);
			return retNodes;
		}
		
		//check the class name + method name
		String oldNodeClassMethodName = oldNode.getMethod().getDeclaringClass().getName().getClassName().toString()
		     + oldNode.getMethod().getDescriptor().toString();
		for(CGNode node : this.cgNew) {
			String classMethodName = node.getMethod().getDeclaringClass().getName().getClassName().toString()
			    + node.getMethod().getDescriptor().toString();
			if(oldNodeClassMethodName.equals(classMethodName)) {
				System.out.println("Use the class + method matching");
				retNodes.add(node);
				return retNodes;
			}
		}
		
		//use invoking methods
		
		
		//use clone detection
		
		//use jdiff matching algorithm?
		
		//use shingle
		return retNodes;
	}
	
	public boolean matchNodes(CGNode oldNode, CGNode newNode, float threshold, int lh) {
		Utils.checkTrue(lh >= 0);
		Utils.checkTrue(threshold >= 0 && threshold <= 1);
		SSACFG oldcfg = oldNode.getIR().getControlFlowGraph();
		SSACFG newcfg = newNode.getIR().getControlFlowGraph();
		
		ISSABasicBlock oldBB = oldcfg.entry();
		ISSABasicBlock newBB = newcfg.entry();
		
		Set<Pair<ISSABasicBlock, ISSABasicBlock>> matched =
			new LinkedHashSet<Pair<ISSABasicBlock, ISSABasicBlock>>();
		
		Stack<Pair<ISSABasicBlock, ISSABasicBlock>> stack
		    = new Stack<Pair<ISSABasicBlock, ISSABasicBlock>>();
		stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(oldBB, newBB));
		
		while(!stack.isEmpty()) {
			Pair<ISSABasicBlock, ISSABasicBlock> pair = stack.pop();
			if(matched.contains(pair)) {
				continue;
			}
			Pair<ISSABasicBlock, ISSABasicBlock> matchedPair = null;
			if(this.comp(pair.a, pair.b, threshold)) {
				matchedPair = pair;
			} else {
				//initialize two lists
				Set<ISSABasicBlock> set1 = new LinkedHashSet<ISSABasicBlock>();
				set1.add(pair.a);
				Set<ISSABasicBlock> set2 = new LinkedHashSet<ISSABasicBlock>();
				set2.add(pair.b);
				for(int i = 0; i < lh; i++) {
					//expand both set1 and set2 for i steps
					Set<ISSABasicBlock> succSet1 = new LinkedHashSet<ISSABasicBlock>();
					for(ISSABasicBlock bb : set1) {
						Iterator<ISSABasicBlock> iter = oldcfg.getSuccNodes(bb);
						while(iter.hasNext()) {
							succSet1.add(iter.next());
						}
					}
					//add to the set
					set1.addAll(succSet1);
					
					Set<ISSABasicBlock> succSet2 = new LinkedHashSet<ISSABasicBlock>();
					for(ISSABasicBlock bb : set2) {
						Iterator<ISSABasicBlock> iter = newcfg.getSuccNodes(bb);
						while(iter.hasNext()) {
							succSet2.add(iter.next());
						}
					}
					set2.addAll(succSet2);
					
					//then start to match
					for(ISSABasicBlock bb2 : succSet2) {
						if(this.comp(pair.a, bb2, threshold)) {
							matchedPair = new Pair<ISSABasicBlock, ISSABasicBlock>(pair.a, bb2);
							break;
						}
					}
					
					if(matchedPair == null) {
						for(ISSABasicBlock bb1 : succSet1) {
							if(this.comp(bb1, pair.b, threshold)) {
								matchedPair = new Pair<ISSABasicBlock, ISSABasicBlock>(bb1, pair.b);
								break;
							}
						}
					}
					
					if(matchedPair != null) {
						break;
					}
				}
				//record the matched pair
				if(matchedPair != null) {
					stack.push(matchedPair); //the matched pair
				}
				
				//add the following nodes of pair.a, pair.b to the stack
				List<ISSABasicBlock> succBB1 = WALAUtils.getSuccBasicBlocks(oldNode, pair.a);
				List<ISSABasicBlock> succBB2 = WALAUtils.getSuccBasicBlocks(newNode, pair.b);
				Utils.checkTrue(succBB1.size() <= 2 && succBB2.size() <= 2);
				
				if(succBB1.isEmpty() || succBB2.isEmpty()) {
					continue;
				} else if (succBB1.size() == 1 && succBB2.size() == 1) {
					stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(succBB1.get(0), succBB2.get(0)));
				} else if (succBB1.size() == 1 && succBB2.size() == 2) {
					stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(succBB1.get(0), succBB2.get(0)));
					stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(succBB1.get(0), succBB2.get(1)));
				} else if(succBB1.size() == 2 && succBB2.size() == 1) {
					stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(succBB1.get(0), succBB2.get(0)));
					stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(succBB1.get(1), succBB2.get(0)));
				} else if (succBB1.size() == 2 && succBB2.size() == 2) {
					stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(succBB1.get(0), succBB2.get(0)));
					stack.push(new Pair<ISSABasicBlock, ISSABasicBlock>(succBB1.get(1), succBB2.get(1)));
				} else {
					throw new Error();
				}
				
			}
		}
		
		return false;
	}
	
	private boolean comp(ISSABasicBlock c1, ISSABasicBlock c2, float threshold) {
		if((c1.isEntryBlock() && c2.isEntryBlock()) || (c1.isExitBlock() && c2.isExitBlock())) {
			return true;
		}
		if((c1.isEntryBlock() && c2.isExitBlock()) || (c1.isExitBlock() && c2.isEntryBlock())) {
			return false;
		}
		
		//check each instructions
		List<SSAInstruction> ssalist1 = WALAUtils.getAllIRs(c1);
		int matchedCount = 0;
		for(SSAInstruction ssa : ssalist1) {
			if(CodeAnalyzer.containInstruction(c2, ssa)) {
				matchedCount++;
			}
		}
		
		float percentage = 0.0f;
		if(ssalist1.isEmpty()) {
			percentage = 1.0f;
		} else {
			percentage = (float)matchedCount/(float)ssalist1.size();
		}
		
		return percentage >= threshold;
	}
}
