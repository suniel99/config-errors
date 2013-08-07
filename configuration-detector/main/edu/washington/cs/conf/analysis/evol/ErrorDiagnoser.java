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
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class ErrorDiagnoser {
	
	public final ExecutionTrace oldTrace;
	public final ExecutionTrace newTrace;
	
	public final CodeAnalyzer oldCoder;
	public final CodeAnalyzer newCoder;
	
	private final ConfEntityRepository oldRep;
	private final ConfEntityRepository newRep;
	
	public final TracesWrapper traceWrapper;
	
	private float pruneThreshold = 0.1f;
	private boolean debug = true;
	
	private Collection<ConfPropOutput> oldSliceOutputs = null;
	private Collection<ConfPropOutput> newSliceOutputs = null;
	
//	public final IterativeSlicer oldSlicer;
//	public final IterativeSlicer newSlicer;
//	private final TraceComparator comparator;
	
	public ErrorDiagnoser(ConfEntityRepository oldConfs, ConfEntityRepository newConfs,
			CodeAnalyzer oldCoder, CodeAnalyzer newCoder, TracesWrapper wrapper) {
		Utils.checkNotNull(oldConfs);
		Utils.checkNotNull(newConfs);
		Utils.checkNotNull(oldCoder);
		Utils.checkNotNull(oldCoder.getCallGraph());
		Utils.checkNotNull(newCoder);
		Utils.checkNotNull(newCoder.getCallGraph());
		Utils.checkNotNull(wrapper);
		this.oldRep = oldConfs;
		this.newRep = newConfs;
		this.oldTrace = new ExecutionTrace(wrapper.oldHistoryFile, wrapper.oldSigFile, wrapper.oldPredicateFile);
		this.newTrace = new ExecutionTrace(wrapper.newHistoryFile, wrapper.newSigFile, wrapper.newPredicateFile);
		this.oldCoder = oldCoder;
		this.newCoder = newCoder;
		this.traceWrapper = wrapper;
//		this.oldSlicer = new IterativeSlicer(this.oldCoder);
//		this.newSlicer = new IterativeSlicer(this.newCoder);
//		this.comparator = new TraceComparator(this.oldTrace, this.newTrace);
	}
	
	/**a ranked list of suspicious configuration options
	//TODO the main entry
	//1. take the predicate execution delta into account
	//   predicate p1, execute 10 times, in which 3 times evaluate to true
	//   compute a value from this and multiple with the cost
	//2. for a predicate only executed in the old version, inform users that
	//   it may not take effect any more.
	//3. consider nested branches?
	 * */
	public List<ConfEntity> diagnoseRootCauses() {
		//perform slicing
		this.performThinSlicing();
		
		//get predicates executed in the old version
		Collection<PredicateExecInfo>  oldPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(this.traceWrapper.oldPredicateFile,
     		    this.traceWrapper.oldSigFile);
	    Collection<PredicateExecInfo> newPredExecs
	        = ExecutionTraceReader.createPredicateExecInfoList(this.traceWrapper.newPredicateFile,
	    		this.traceWrapper.newSigFile);
	    
	    //the matched predicates
	    Set<PredicateBehaviorAcrossVersions> matchedPreds
	        = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, this.oldCoder, this.newCoder);
	    //rank by the behavior changes
	    matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
	    
	    //store the likelihood of each configuration option in a map
	    Map<ConfEntity, Float> oldConfMap = new LinkedHashMap<ConfEntity, Float>();
	    Map<ConfEntity, Float> newConfMap = new LinkedHashMap<ConfEntity, Float>();
	    
		for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
			float behaviorDiff = predBehavior.getDifferenceDegree();
			if(behaviorDiff < pruneThreshold) {
				continue;
			}
			int instrNumOnOldVersion = 0;
			int instrNumOnNewVersion = 0;
			if(predBehavior.isExecutedOnOldVersion()) {
				Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, predBehavior.createOldPredicateExecInfo());
				instrNumOnOldVersion = set.size();
			}
			if(predBehavior.isExecutedOnNewVersion()) {
				Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, predBehavior.createNewPredicateExecInfo());
				instrNumOnNewVersion = set.size();
			}
			
			int instrDelta = Math.abs(instrNumOnNewVersion - instrNumOnOldVersion);
			float behaviorDelta = instrDelta*behaviorDiff;
			
			Set<ConfEntity> oldConfs = this.getAffectingOptionsInOldVersion(predBehavior.oldMethodSig, predBehavior.oldIndex);
			Set<ConfEntity> newConfs = this.getAffectingOptionsInNewVersion(predBehavior.newMethodSig, predBehavior.newIndex);

			//update the likelihood
			for(ConfEntity oldConf : oldConfs) {
				if(!oldConfMap.containsKey(oldConf)) {
					oldConfMap.put(oldConf, behaviorDelta);
				} else {
					oldConfMap.put(oldConf, oldConfMap.get(oldConf) + behaviorDelta);
				}
			}
			for(ConfEntity newConf : newConfs) {
				if(!newConfMap.containsKey(newConf)) {
					newConfMap.put(newConf, behaviorDelta);
				} else {
					newConfMap.put(newConf, newConfMap.get(newConf) + behaviorDelta);
				}
			}
			
			
			if(debug) {
			    System.out.println(predBehavior);
			    System.out.println("      diff: " + behaviorDiff);
			    System.out.println("      executed ssa on old: " + instrNumOnOldVersion);
			    System.out.println("      executed ssa on new: " + instrNumOnNewVersion);
			    System.out.println("      behavior delta: " + behaviorDelta);
			    System.out.println();
			}
		}
		
		oldConfMap = Utils.sortByValue(oldConfMap, false);
		newConfMap = Utils.sortByValue(newConfMap, false);
		
		System.out.println(" ========= Dump the results =========");
		System.out.println(" ========= old version =========");
		for(ConfEntity e : oldConfMap.keySet()) {
			System.out.println(e.getConfName() + " => " + oldConfMap.get(e));
		}
		System.out.println(" ========= new version =========");
		for(ConfEntity e : newConfMap.keySet()) {
			System.out.println(e.getConfName() + " => " + oldConfMap.get(e));
		}
		
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		return list;
	}

	private void performThinSlicing() {
		this.oldSliceOutputs = CommonUtils.getConfPropOutputs(oldCoder.slicer, this.oldRep, false);	
		this.newSliceOutputs = CommonUtils.getConfPropOutputs(newCoder.slicer, this.newRep, false);	
	}
	
	private Set<ConfEntity> getAffectingOptionsInOldVersion(String methodSig, int instructionIndex) {
		Set<ConfEntity> set = new LinkedHashSet<ConfEntity>();
		for(ConfPropOutput oldOutput : this.oldSliceOutputs) {
			if(oldOutput.containStatement(methodSig, instructionIndex)) {
				set.add(oldOutput.conf);
			}
		}
		return set;
	}
	
	private Set<ConfEntity> getAffectingOptionsInNewVersion(String methodSig, int instructionIndex) {
		Set<ConfEntity> set = new LinkedHashSet<ConfEntity>();
		for(ConfPropOutput newOutput : this.newSliceOutputs) {
			if(newOutput.containStatement(methodSig, instructionIndex)) {
				set.add(newOutput.conf);
			}
		}
		return set;
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