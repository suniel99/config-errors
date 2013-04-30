package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.util.WALAUtils;

public class CodeAnalyzer {

	public final ConfigurationSlicer slicer;
	
	public CodeAnalyzer(String classPath, String mainClass) {
		this.slicer = new ConfigurationSlicer(classPath, mainClass);
	}
	
	public void buildAnalysis() {
		this.slicer.buildAnalysis();
	}
	
	public SSAInstruction getInstruction(String methodSig, int index) {
		return getInstruction(this.slicer.getCallGraph(), methodSig, index);
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
	
	public String getAllInstruction(String methodSig) {
		for(CGNode node : this.slicer.getCallGraph()) {
			if(node.getMethod().getSignature().equals(methodSig)) {
				return WALAUtils.getAllIRAsString(node);
			}
		}
		return null;
	}
}
