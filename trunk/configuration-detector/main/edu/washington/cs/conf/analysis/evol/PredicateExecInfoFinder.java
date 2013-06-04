package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class PredicateExecInfoFinder {

	public static PredicateExecInfo findPredicate(Collection<PredicateExecInfo> coll,
			CGNode hostMethod, SSAInstruction ssa) {
		String hostMethodSig = hostMethod.getMethod().getSignature();
		int index = WALAUtils.getInstructionIndex(hostMethod, ssa);
		Utils.checkTrue(index != -1, "The host method does not contain the ssa.");
		//iterate through the predicate exec info pool, and find the matched ssa
		for(PredicateExecInfo predicateExec : coll) {
			String methodSig = predicateExec.getMethod();
			int predIndex = predicateExec.getIndex();
			if(hostMethodSig.equals(methodSig) && index == predIndex) {
				return predicateExec;
			}
		}
		return null;
	}
	
}