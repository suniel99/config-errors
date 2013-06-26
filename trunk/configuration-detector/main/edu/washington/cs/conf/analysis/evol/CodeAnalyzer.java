package edu.washington.cs.conf.analysis.evol;

import java.util.List;

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

import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class CodeAnalyzer {

	public final ConfigurationSlicer slicer;
	
	public CodeAnalyzer(String classPath, String mainClass) {
		this.slicer = new ConfigurationSlicer(classPath, mainClass);
	}
	
	public void buildAnalysis() {
		this.slicer.buildAnalysis();
	}
	
	public CallGraph getCallGraph() {
		return this.slicer.getCallGraph();
	}
	
	public boolean hasNode(CGNode node) {
		Utils.checkNotNull(node, "not existed: " + node.getMethod().getSignature());
		for(CGNode n : this.getCallGraph()) {
			if(n.getMethod().getSignature().equals(node.getMethod().getSignature())) {
				return true;
			}
		}
		return false;
	}
	
	public SSAInstruction getInstruction(String methodSig, int index) {
		return CodeAnalysisUtils.getInstruction(this.slicer.getCallGraph(), methodSig, index);
	}
	
	public String getAllInstruction(String methodSig) {
		for(CGNode node : this.slicer.getCallGraph()) {
			if(node.getMethod().getSignature().equals(methodSig)) {
				return WALAUtils.getAllIRAsString(node);
			}
		}
		return null;
	}
}
