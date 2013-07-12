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
	public List<ConfEntity> diagnoseRootCauses() {
		
		Set<PredicateExecution> oldPredicates
		    = this.comparator.getPredicateOnlyExecutedInOldVersion();
		Set<PredicateExecution> newPredicates
		    = this.comparator.getPredicateOnlyExecutedInNewVersion();
		Set<PredicateExecution> diffPredicates
		    = this.comparator.getPredicateWithDifferentBehaviors();
		
		Map<ConfEntity, Float> oldOptions = new LinkedHashMap<ConfEntity, Float>();
		Map<ConfEntity, Float> newOptions = new LinkedHashMap<ConfEntity, Float>();
		
		//FIXME here
		Set<SSAInstruction> executedDiffSSAs
		    = this.computeDiffInstructions(this.oldCoder, this.oldTrace, oldPredicates);
		for(PredicateExecution oldPredicate : oldPredicates) {
			SSAInstruction ssa = oldPredicate.getInstruction(this.oldCoder);
			CGNode node = oldPredicate.getNode(this.oldCoder);
			int affectedCost = this.oldSlicer.compute_cost_by_slice(node, ssa, executedDiffSSAs);
			Set<ConfEntity> entities = this.getAffectingOptions(this.oldCoder, node, ssa);
			for(ConfEntity conf : entities) {
				if(!oldOptions.containsKey(conf)) {
					oldOptions.put(conf, (float)affectedCost);
				} else {
					oldOptions.put(conf, (float)affectedCost + oldOptions.get(conf));
				}
			}
		}
		
		//FIXME
		executedDiffSSAs
		    = this.computeDiffInstructions(this.newCoder, this.newTrace, newPredicates);
		for(PredicateExecution newPredicate : newPredicates) {
			SSAInstruction ssa = newPredicate.getInstruction(this.newCoder);
			CGNode node = newPredicate.getNode(this.newCoder);
			int affectedCost = this.newSlicer.compute_cost_by_slice(node, ssa, executedDiffSSAs);
			Set<ConfEntity> entities = this.getAffectingOptions(this.newCoder, node, ssa);
			for(ConfEntity conf : entities) {
				if(!newOptions.containsKey(conf)) {
					newOptions.put(conf, (float)affectedCost);
				} else {
					newOptions.put(conf, (float)affectedCost + newOptions.get(conf));
				}
			}
		}
		
		//FIXME, the code is redundant below, but I keep the redundancy now, since
		//it may need to tweak the cost here.
		executedDiffSSAs
		    = this.computeDiffInstructions(this.newCoder, this.newTrace, newPredicates);
		for(PredicateExecution diffPredicate : diffPredicates) {
			SSAInstruction ssa = diffPredicate.getInstruction(this.newCoder);
			CGNode node = diffPredicate.getNode(this.newCoder);
			int affectedCost = this.newSlicer.compute_cost_by_slice(node, ssa, executedDiffSSAs);
			Set<ConfEntity> entities = this.getAffectingOptions(this.newCoder, node, ssa);
			for(ConfEntity conf : entities) {
				if(!newOptions.containsKey(conf)) {
					newOptions.put(conf, (float)affectedCost);
				} else {
					newOptions.put(conf, (float)affectedCost + newOptions.get(conf));
				}
			}
		}
		
		Map<ConfEntity, Float> summary = new LinkedHashMap<ConfEntity, Float>();
		for(ConfEntity conf : oldOptions.keySet()) {
			summary.put(conf, oldOptions.get(conf));
		}
		for(ConfEntity conf : newOptions.keySet()) {
			if(!summary.containsKey(conf)) {
				summary.put(conf, newOptions.get(conf));
			} else {
				summary.put(conf, summary.get(conf) + newOptions.get(conf));
			}
		}
		
		
		summary = Utils.sortByValue(summary, false);
		List<ConfEntity> options = new LinkedList<ConfEntity>(summary.keySet());
		
		return options;
	}
	
	Set<SSAInstruction> computeDiffInstructions(CodeAnalyzer coder,
			ExecutionTrace trace, Set<PredicateExecution> predSet) {
		Set<SSAInstruction> ssaSet = new LinkedHashSet<SSAInstruction>();
		for(PredicateExecution exec : predSet) {
			CGNode node = exec.getNode(coder);
			SSAInstruction ssa = exec.getInstruction(coder);
			SSAInstruction postSSA
			    = PostDominatorFinder.getImmediatePostDominatorInstruction(node, ssa);
			String startMethodSig = node.getMethod().getSignature();
			String endMethodSig = startMethodSig;
			int startIndex = WALAUtils.getInstructionIndex(node, ssa);
			int endIndex = WALAUtils.getInstructionIndex(node, postSSA);
			Set<InstructionExecInfo> execSSAs
			    = trace.getExecutedInstructions(startMethodSig, startIndex, endMethodSig, endIndex);
			for(InstructionExecInfo execSSA : execSSAs) {
				CGNode ssaNode = execSSA.getNode(coder);
				SSAInstruction executedSSA = execSSA.getInstruction(ssaNode);
				ssaSet.add(executedSSA);
			}
		}
		
		return ssaSet;
	}
	
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