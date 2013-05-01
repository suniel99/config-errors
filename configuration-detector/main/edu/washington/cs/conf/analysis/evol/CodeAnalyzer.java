package edu.washington.cs.conf.analysis.evol;

import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
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
	
	public CallGraph getCallGraph() {
		return this.slicer.getCallGraph();
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
	
	public static boolean containInstruction(ISSABasicBlock bb, SSAInstruction ssa) {
		Class<?> ssaType = ssa.getClass();
		List<SSAInstruction> ssaList = WALAUtils.getAllIRs(bb);
		boolean hasSameType = false;
		for(SSAInstruction instruction : ssaList) {
			if(instruction != null && instruction.getClass().equals(ssaType)) {
				hasSameType = true;
			}
		}
		if(!hasSameType) {
			return false;
		}
		//further the method call
		boolean hasSameMethod = false;
		if(ssa instanceof SSAAbstractInvokeInstruction) {
			SSAAbstractInvokeInstruction invokeInstruction = (SSAAbstractInvokeInstruction)ssa;
			String methodName = invokeInstruction.getCallSite().getDeclaredTarget().getName().toString();
			for(SSAInstruction instruction : ssaList) {
				if(instruction instanceof SSAAbstractInvokeInstruction) {
					SSAAbstractInvokeInstruction innerInvoke = (SSAAbstractInvokeInstruction)instruction;
					String innverMethodName = innerInvoke.getCallSite().getDeclaredTarget().getName().toString();
					if(methodName.equals(innverMethodName)) {
						hasSameMethod = true;
						break;
					}
				}
			}
		}
		
		//XXX should also check read / write field
		//XXX or use a better approximate way, e.g., use the matching percentage
		
		return hasSameMethod;
	}
}
