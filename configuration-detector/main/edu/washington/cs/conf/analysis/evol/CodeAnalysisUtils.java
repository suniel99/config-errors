package edu.washington.cs.conf.analysis.evol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAArrayReferenceInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAComparisonInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAConversionInstruction;
import com.ibm.wala.ssa.SSAFieldAccessInstruction;
import com.ibm.wala.ssa.SSAGetCaughtExceptionInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAMonitorInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.ssa.SSAThrowInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class CodeAnalysisUtils {
	/***
	 * All static methods below
	 * */
	public static boolean isPredicateInstruction(SSAInstruction ssa) {
		return ssa instanceof SSAConditionalBranchInstruction;
	}
	
	public static SSAInstruction getInstruction(CallGraph cg, String methodSig, int index) {
		for(CGNode node : cg) {
			if(node.getMethod().getSignature().equals(methodSig)) {
				SSAInstruction[] instructions = node.getIR().getInstructions();
				if(index < 0 || index >= instructions.length) {
					throw new Error("The index: " + index + " exceeds the max: " + instructions.length
							+ " in method: " + methodSig);
				}
				return instructions[index];
			}
		}
		return null;
	}
	
	public static boolean approxContainInstruction(ISSABasicBlock bb, SSAInstruction ssa) {
		List<SSAInstruction> ssaList = WALAUtils.getAllIRs(bb);
		for(SSAInstruction instruction : ssaList) {
			if(approxSameInstruction(ssa, instruction)) {
				return true;
			}
		}
		return false;
	}

	
	//Approximate comparison
	public static boolean approxSameInstruction(SSAInstruction ssa1, SSAInstruction ssa2) {
		if(!ssa1.getClass().equals(ssa2.getClass())) {
			return false;
		}
		//the case of different types
		if(ssa1 instanceof SSAAbstractInvokeInstruction) {
			SSAAbstractInvokeInstruction invoke1 = (SSAAbstractInvokeInstruction)ssa1;
			SSAAbstractInvokeInstruction invoke2 = (SSAAbstractInvokeInstruction)ssa2;
			String method1 = invoke1.getCallSite().getDeclaredTarget().getName().toString();
			String method2 = invoke2.getCallSite().getDeclaredTarget().getName().toString();
			return method1.equals(method2);
		} else if (ssa1 instanceof SSAFieldAccessInstruction) {
			SSAFieldAccessInstruction field1 = (SSAFieldAccessInstruction)ssa1;
			SSAFieldAccessInstruction field2 = (SSAFieldAccessInstruction)ssa2;
			return field1.getDeclaredField().getSignature().equals(field2.getDeclaredField().getSignature());
		} else if (ssa1 instanceof SSAThrowInstruction) {
//			SSAThrowInstruction throw1 = (SSAThrowInstruction)ssa1;
//			SSAThrowInstruction throw2 = (SSAThrowInstruction)ssa2;
			//XXX
		} else if (ssa1 instanceof SSAArrayReferenceInstruction) {
			SSAArrayReferenceInstruction arrayRef1 = (SSAArrayReferenceInstruction)ssa1;
			SSAArrayReferenceInstruction arrayRef2 = (SSAArrayReferenceInstruction)ssa2;
			String type1 = arrayRef1.getElementType().getName().toString();
			String type2 = arrayRef2.getElementType().getName().toString();
			return type1.equals(type2);
		} else if (ssa1 instanceof SSACheckCastInstruction) {
//			SSACheckCastInstruction cast1 = (SSACheckCastInstruction)ssa1;
//			SSACheckCastInstruction cast2 = (SSACheckCastInstruction)ssa2;
			
		} else if (ssa1 instanceof SSAConversionInstruction) {
			SSAConversionInstruction conv1 = (SSAConversionInstruction)ssa1;
			SSAConversionInstruction conv2 = (SSAConversionInstruction)ssa2;
			String from1 = conv1.getFromType().getName().toString();
			String from2 = conv2.getFromType().getName().toString();
			String to1 = conv1.getToType().getName().toString();
			String to2 = conv2.getToType().getName().toString();
			return from1.equals(from2) && to1.equals(to2);
		} else if (ssa1 instanceof SSANewInstruction) {
			SSANewInstruction new1 = (SSANewInstruction)ssa1;
			SSANewInstruction new2 = (SSANewInstruction)ssa2;
			String type1 = new1.getConcreteType().getName().getClassName().toString();
			String type2 = new2.getConcreteType().getName().getClassName().toString();
			return type1.equals(type2);
		} else if (ssa1 instanceof SSABinaryOpInstruction) {
			SSABinaryOpInstruction binOp1 = (SSABinaryOpInstruction)ssa1;
			SSABinaryOpInstruction binOp2 = (SSABinaryOpInstruction)ssa2;
			return binOp1.getOperator().equals(binOp2.getOperator());
		} else if (ssa1 instanceof SSAComparisonInstruction) {
			SSAComparisonInstruction cmp1 = (SSAComparisonInstruction)ssa1;
			SSAComparisonInstruction cmp2 = (SSAComparisonInstruction)ssa2;
			return cmp1.getOperator().equals(cmp2.getOperator());
		} else if (ssa1 instanceof SSAGetCaughtExceptionInstruction) {
			//XXX exception
		} else if (ssa1 instanceof SSAInstanceofInstruction) {
			SSAInstanceofInstruction instance1 = (SSAInstanceofInstruction)ssa1;
			SSAInstanceofInstruction instance2 = (SSAInstanceofInstruction)ssa2;
			String type1 = instance1.getCheckedType().getName().getClassName().toString();
			String type2 = instance2.getCheckedType().getName().getClassName().toString();
			return type1.equals(type2);
		} else if (ssa1 instanceof SSAMonitorInstruction) { //XXX approx
			SSAMonitorInstruction mon1 = (SSAMonitorInstruction)ssa1;
			SSAMonitorInstruction mon2 = (SSAMonitorInstruction)ssa2;
			return mon1.isMonitorEnter() == mon2.isMonitorEnter();
		} else if (ssa1 instanceof SSAReturnInstruction) {  //XXX approx
			SSAReturnInstruction ret1 = (SSAReturnInstruction)ssa1;
			SSAReturnInstruction ret2 = (SSAReturnInstruction)ssa2;
			return ret1.returnsPrimitiveType() == ret2.returnsPrimitiveType()
			    && ret1.returnsVoid() == ret2.returnsVoid();
		}
		return true; //for other cases
	}
	
	//FIXME the result can be cached
	public static boolean post_dom_debug = true;
	public static ISSABasicBlock computeImmediatePostDominator(CGNode node, ISSABasicBlock block) {
		List<ISSABasicBlock> listBBs = WALAUtils.getAllBasicBlocks(node);
		Map<ISSABasicBlock, Set<ISSABasicBlock>> postDom = new HashMap<ISSABasicBlock, Set<ISSABasicBlock>>();
		Utils.checkTrue(listBBs.contains(block));
		
		//the initial set only contain the basic block itself
		for(ISSABasicBlock bb : listBBs) {
			Set<ISSABasicBlock> set = new HashSet<ISSABasicBlock>();
			set.add(bb);
			postDom.put(bb, set);
		}
		
		//iterate until no changes
		boolean changed = true;
		while(changed) {
			changed = false;
			for(ISSABasicBlock bb : listBBs) {
				if(post_dom_debug) {
				    System.out.println("processing: " + bb.getNumber());
				}
				List<ISSABasicBlock> succs = WALAUtils.getSuccBasicBlocks(node, bb);
				//must exclude exit node
				List<ISSABasicBlock> noExitSuccs = new LinkedList<ISSABasicBlock>();
				for(ISSABasicBlock succ : succs) {
					if(!succ.isExitBlock()) {
						noExitSuccs.add(succ);
					}
				}
				succs.clear();
				succs.addAll(noExitSuccs);
				if(post_dom_debug) {
				    System.out.println("  succ id, exluding exit: "
						+ WALAUtils.getAllBasicBlockIDList(noExitSuccs));
				}
				
				//compute the intersection of all BBs of succ nodes
				Set<ISSABasicBlock> intersectBBs = new HashSet<ISSABasicBlock>();
				if(!succs.isEmpty()) {
					
					for(ISSABasicBlock succ : succs) {
						Set<ISSABasicBlock> domSet = postDom.get(succ);
						Set<ISSABasicBlock> otherDom = new HashSet<ISSABasicBlock>();
						otherDom.addAll(domSet);
						otherDom.remove(succ); //remove itself
						if(domSet.contains(bb) && postDom.get(bb).containsAll(otherDom)) {
							if(post_dom_debug) {
							    System.out.println("   skip loop back edge: " + succ.getNumber()
									+ ", whose domset: " + WALAUtils.getAllBasicBlockIDList(domSet)
									+ ", the current update node: " + bb.getNumber()
									+ ", whose domset: " + WALAUtils.getAllBasicBlockIDList(postDom.get(bb)));
							}
							continue; //manage the loop
						}
						//if the intersectBBs is empty
						if(intersectBBs.isEmpty()) {
							intersectBBs.addAll(domSet);
						} else {
							//do intersection
							intersectBBs = Utils.intersect(intersectBBs, domSet);
						}
						if(post_dom_debug) {
						    System.out.println("   intersect: " + WALAUtils.getAllBasicBlockIDList(intersectBBs));
						}
					}
				}
				//check the bb size and update the dominate tree
				int originalSize = postDom.get(bb).size();
				postDom.get(bb).addAll(intersectBBs);
				if(originalSize != postDom.get(bb).size()) {
					changed = true;
					if(post_dom_debug) {
					    System.out.println("++ update: " + bb.getNumber() + ": "
							+ WALAUtils.getAllBasicBlockIDList(postDom.get(bb)));
					}
				}
			}
		}
		
		//get the post-dominate set
		Set<ISSABasicBlock> postDomSet = postDom.get(block);
		Utils.checkTrue(postDomSet.size() > 0);
		
		Set<ISSABasicBlock> excludedDomSet = new LinkedHashSet<ISSABasicBlock>();
		
		//need to exclude itself and the exit block
		for(ISSABasicBlock bb : postDomSet) {
			if(!bb.isExitBlock() && bb != block) {
				excludedDomSet.add(bb);
			}
		}
		
		//do a BFS from block, and to check which basicblock seems first
		ISSABasicBlock immediatePost = null;
		//the first traversed basic block is the immediate one
		List<ISSABasicBlock> queue = new LinkedList<ISSABasicBlock>();
		Set<ISSABasicBlock> visited = new HashSet<ISSABasicBlock>();
		queue.addAll(WALAUtils.getSuccBasicBlocks(node, block));
		while(!queue.isEmpty()) {
			ISSABasicBlock currBB = queue.remove(0);
			if(visited.contains(currBB)) {
				continue;
			}
			if(currBB != block && excludedDomSet.contains(currBB)) {
				immediatePost = currBB;
				break;
			}
			visited.add(currBB);
			for(ISSABasicBlock nextBB : WALAUtils.getSuccBasicBlocks(node, currBB)) {
				queue.add(nextBB);
			}
		}
		
		//if no immediate post basic block is available,
		//use the exit block
		if(immediatePost != null) {
			return immediatePost;
		} else {
			return node.getIR().getExitBlock();
		}
	}
}
