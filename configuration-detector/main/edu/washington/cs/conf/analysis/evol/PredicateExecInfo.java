package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class PredicateExecInfo {

	public final String context; //the outside method
	public final String predicate; //the predicate itself
	public final int evalFreqCount;
	public final int evalResultCount;
	
	public PredicateExecInfo(String context, String predicate, int freq, int result) {
		Utils.checkNotNull(context);
		Utils.checkNotNull(predicate);
		Utils.checkTrue(freq > 0);
		Utils.checkTrue(result >= 0);
		this.context = context;
		this.predicate = predicate;
		this.evalFreqCount = freq;
		this.evalResultCount = result;
	}
	
	public String getMethodSig() {
		return this.context;
	}
	
	public int getIndex() {
		return Integer.parseInt(this.predicate);
	}
	
	//a sample line:
	//randoop.util.Reflection.isVisible(Ljava/lang/Class;)Z##11==1:0
	public static PredicateExecInfo createPredicateExecInfo(String line) {
		Utils.checkNotNull(line);
		String[] splits = line.split(EfficientTracer.SEP);
		String context = splits[0];
		String[] indexAndEval = splits[1].split(EfficientTracer.PRED_SEP);
		String predicate = indexAndEval[0];
		String[] results = indexAndEval[1].split(EfficientTracer.EVAL_SEP);
		Integer freq = Integer.parseInt(results[0]);
		Integer eval = Integer.parseInt(results[1]);
		Utils.checkTrue(freq >= eval);
		return new PredicateExecInfo(context, predicate, freq, eval);
	}
	
	public static Collection<PredicateExecInfo> createPredicateExecInfoList(String fileName) {
		Collection<PredicateExecInfo> coll = new LinkedList<PredicateExecInfo>();
		List<String> fileContent = Files.readWholeNoExp(fileName);
		for(String line : fileContent) {
			if(line.trim().isEmpty()) {
				continue;
			}
			PredicateExecInfo execInfo = createPredicateExecInfo(line);
			coll.add(execInfo);
		}
		return coll;
	}
	
	//this is just for debugging purpose
	public void showContext(CallGraph cg) {
		this.showContext(WALAUtils.lookupMatchedCGNode(cg, this.getMethodSig()));
	}
	public void showContext(CGNode node) {
		Utils.checkNotNull(node);
		Utils.checkTrue(this.getMethodSig().equals(node.getMethod().getSignature()));
		WALAUtils.printCFG(node);
		SSAInstruction ssa = node.getIR().getInstructions()[this.getIndex()];
		System.out.println("The " + this.getIndex() + "-th instruction is: " + ssa);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof PredicateExecInfo)) {
			return false;
		}
		PredicateExecInfo info = (PredicateExecInfo)o;
		return this.context.equals(info.context) && this.predicate.equals(info.predicate)
		    && this.evalFreqCount == info.evalFreqCount && this.evalResultCount == info.evalResultCount;
	}
	
	@Override
	public int hashCode() {
		return this.evalFreqCount + 7*this.evalResultCount + 17*this.context.hashCode()
		    + 29*this.predicate.hashCode();
	}
	
	@Override
	public String toString() {
		return predicate + "@" + context + " -> " + evalFreqCount + ":" + evalResultCount;
	}
}