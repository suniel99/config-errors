package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.util.Utils;

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