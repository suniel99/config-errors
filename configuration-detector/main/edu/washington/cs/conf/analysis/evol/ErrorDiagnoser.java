package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class ErrorDiagnoser {
	
	public final ExecutionTrace oldTrace;
	public final ExecutionTrace newTrace;
	
	public final CodeAnalyzer oldCoder;
	public final CodeAnalyzer newCoder;
	
	private final Set<ConfEntity> oldConfigs = new LinkedHashSet<ConfEntity>();
	private final Set<ConfEntity> newConfigs = new LinkedHashSet<ConfEntity>();
	
	public final IterativeSlicer oldSlicer;
	public final IterativeSlicer newSlicer;
	
	private final TraceComparator comparator;
	
	public ErrorDiagnoser(ExecutionTrace oldTrace, ExecutionTrace newTrace,
			CodeAnalyzer oldCoder, CodeAnalyzer newCoder) {
		Utils.checkNotNull(oldTrace);
		Utils.checkNotNull(newTrace);
		Utils.checkNotNull(oldCoder);
		Utils.checkNotNull(newCoder);
		this.oldTrace = oldTrace;
		this.newTrace = newTrace;
		this.oldCoder = oldCoder;
		this.newCoder = newCoder;
		this.oldSlicer = new IterativeSlicer(this.oldCoder);
		this.newSlicer = new IterativeSlicer(this.newCoder);
		this.comparator = new TraceComparator(this.oldTrace, this.newTrace);
	}
	
	//a ranked list of suspicious configuration options
	//TODO the main entry
	//1. take the predicate execution delta into account
	//   predicate p1, execute 10 times, in which 3 times evaluate to true
	//   compute a value from this and multiple with the cost
	//2. for a predicate only executed in the old version, inform users that
	//   it may not take effect any more.
	//3. consider nested branches?
	public List<ConfEntity> diagnoseRootCauses() {
		
//		Set<PredicateBehaviorAcrossVersions> oldPredicates
//		    = this.comparator.getPredicateOnlyExecutedInOldVersion();
//		Set<PredicateBehaviorAcrossVersions> newPredicates
//		    = this.comparator.getPredicateOnlyExecutedInNewVersion();
//		Set<PredicateBehaviorAcrossVersions> diffPredicates
//		    = this.comparator.getPredicateWithDifferentBehaviors();
//		
//		//storing the options and their weights
//		Map<ConfEntity, Float> oldOptions = new LinkedHashMap<ConfEntity, Float>();
//		Map<ConfEntity, Float> newOptions = new LinkedHashMap<ConfEntity, Float>();
//		
//		//FIXME here
//		//here, WRONG, the executedSSAs should be all ssas executed, but within
//		//behaviorally-different branches
//		Set<SSAInstruction> executedSSAs
//		    = this.computeExecutedInstructionsInPredicates(this.oldCoder, this.oldTrace, oldPredicates);
//		for(PredicateBehaviorAcrossVersions oldPredicate : oldPredicates) {
//			SSAInstruction ssa = oldPredicate.getInstruction(this.oldCoder);
//			CGNode node = oldPredicate.getNode(this.oldCoder);
//			int affectedCost = this.oldSlicer.compute_cost_by_slice(node, ssa, executedSSAs);
//			Set<ConfEntity> entities = this.getAffectingOptions(this.oldCoder, node, ssa);
//			for(ConfEntity conf : entities) {
//				if(!oldOptions.containsKey(conf)) {
//					oldOptions.put(conf, (float)affectedCost);
//				} else {
//					oldOptions.put(conf, (float)affectedCost + oldOptions.get(conf));
//				}
//			}
//		}
//		
//		//FIXME
//		executedSSAs
//		    = this.computeExecutedInstructionsInPredicates(this.newCoder, this.newTrace, newPredicates);
//		for(PredicateBehaviorAcrossVersions newPredicate : newPredicates) {
//			SSAInstruction ssa = newPredicate.getInstruction(this.newCoder);
//			CGNode node = newPredicate.getNode(this.newCoder);
//			int affectedCost = this.newSlicer.compute_cost_by_slice(node, ssa, executedSSAs);
//			Set<ConfEntity> entities = this.getAffectingOptions(this.newCoder, node, ssa);
//			for(ConfEntity conf : entities) {
//				if(!newOptions.containsKey(conf)) {
//					newOptions.put(conf, (float)affectedCost);
//				} else {
//					newOptions.put(conf, (float)affectedCost + newOptions.get(conf));
//				}
//			}
//		}
//		
//		//FIXME, the code is redundant below, but I keep the redundancy now, since
//		//it may need to tweak the cost here.
//		executedSSAs
//		    = this.computeExecutedInstructionsInPredicates(this.newCoder, this.newTrace, diffPredicates);
//		for(PredicateBehaviorAcrossVersions diffPredicate : diffPredicates) {
//			SSAInstruction ssa = diffPredicate.getInstruction(this.newCoder);
//			CGNode node = diffPredicate.getNode(this.newCoder);
//			int affectedCost = this.newSlicer.compute_cost_by_slice(node, ssa, executedSSAs);
//			Set<ConfEntity> entities = this.getAffectingOptions(this.newCoder, node, ssa);
//			for(ConfEntity conf : entities) {
//				if(!newOptions.containsKey(conf)) {
//					newOptions.put(conf, (float)affectedCost);
//				} else {
//					newOptions.put(conf, (float)affectedCost + newOptions.get(conf));
//				}
//			}
//		}
//		
//		Map<ConfEntity, Float> summary = new LinkedHashMap<ConfEntity, Float>();
//		for(ConfEntity conf : oldOptions.keySet()) {
//			summary.put(conf, oldOptions.get(conf));
//		}
//		for(ConfEntity conf : newOptions.keySet()) {
//			if(!summary.containsKey(conf)) {
//				summary.put(conf, newOptions.get(conf));
//			} else {
//				summary.put(conf, summary.get(conf) + newOptions.get(conf));
//			}
//		}
//		
//		
//		summary = Utils.sortByValue(summary, false);
//		List<ConfEntity> options = new LinkedList<ConfEntity>(summary.keySet());
//		
//		return options;
		
		throw new Error();
	}
	
//	Set<SSAInstruction> computeExecutedInstructionsInPredicates(CodeAnalyzer coder,
//			ExecutionTrace trace, Set<PredicateBehaviorAcrossVersions> predSet) {
//		Set<SSAInstruction> ssaSet = new LinkedHashSet<SSAInstruction>();
//		for(PredicateBehaviorAcrossVersions exec : predSet) {
//			CGNode node = exec.getNode(coder);
//			SSAInstruction ssa = exec.getInstruction(coder);
//			SSAInstruction postSSA
//			    = PostDominatorFinder.getImmediatePostDominatorInstruction(node, ssa);
//			String startMethodSig = node.getMethod().getSignature();
//			String endMethodSig = startMethodSig;
//			int startIndex = WALAUtils.getInstructionIndex(node, ssa);
//			int endIndex = WALAUtils.getInstructionIndex(node, postSSA);
//			Set<InstructionExecInfo> execSSAs
//			    = trace.getExecutedInstructionsBetween(startMethodSig, startIndex, endMethodSig, endIndex);
//			for(InstructionExecInfo execSSA : execSSAs) {
//				CGNode ssaNode = execSSA.getNode(coder);
//				SSAInstruction executedSSA = execSSA.getInstruction(ssaNode);
//				ssaSet.add(executedSSA);
//			}
//		}
//		
//		return ssaSet;
//	}
	
	//TODO
	Set<ConfEntity> getAffectingOptions(CodeAnalyzer coder, CGNode node, SSAInstruction ssa) {
		throw new Error();
	}
}

/**
 * 
 * algorithm sketch:
 * 
 * two execution traces, t_old, t_new
 * 
 * for each predicate in t_old, and t_new, classify it as "only executed in t_old",
 * "only executed in t_new", or "both but differently".
 * 
 * for predicates only executed in one version, merging nested predicates
 * 
 * for each different predicate, compute its cost by counting the number of instructions
 * (what about the cases of nested predicates, e.g., recursive case)
 * - nest case: if nested, just count the first level
 * - diff: instructions executed (|true branch - false branch|)
 *   remove the nested instructions. such as:
 *   
 *   if(x) {  //only count i1, and i2
 *      i1
 *      i2
 *      if(y) {
 *      }
 *   }
 *   
 *   //count both side?
 *   
 * - new: num  (true - false)
 * 
 *   //count both side
 * 
 * - old: num   (true - false)
 * 
 *   //count both side
 * 
 * for each predicate not executed in both versions, find the variables inside,
 * and repeatedly do the slicing
 * - just account for variables inside the basic block  (IGNORED) since both are executed
 * - new: OK natural
 * 
 *   (not both side, only for the executed part)
 * 
 * - old: OK natural
 * 
 * what about "changing the predicate"?? e.g.,
 * 
 * changing  if(pred)   to  if(!pred)
 * 
 * */


/**
 * if there is a lot of redundant computation, which causes the differences,
 * but it is unlikely
 * 
 * */