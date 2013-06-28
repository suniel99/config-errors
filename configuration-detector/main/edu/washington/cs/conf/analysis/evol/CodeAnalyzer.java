package edu.washington.cs.conf.analysis.evol;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAArrayReferenceInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACFG;
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
import com.ibm.wala.ssa.SSAPutInstruction;
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
	
	public ISSABasicBlock findMergedBasicBlock(String methodSig, int index) {
		SSAInstruction ssa = this.getInstruction(methodSig, index);
		Utils.checkTrue(CodeAnalysisUtils.isPredicateInstruction(ssa), "Instruction: " + ssa
				+ " is not conditional branch");
		CGNode node = WALAUtils.lookupMatchedCGNode(getCallGraph(), methodSig);
		
		SSACFG cfg = node.getIR().getControlFlowGraph();
		//find the basic block that contains the given instruction
		
		ISSABasicBlock hostBasicBlock = cfg.getBlockForInstruction(index);
		Utils.checkNotNull(hostBasicBlock);
		
		//iterate the graph to check the merge point
		//the merge point is a basic block that every path from
		//immediate post dominator
		ISSABasicBlock postDominator = PostDominatorFinder.computeImmediatePostDominator(node, hostBasicBlock);
		return postDominator;
	}
	
	public List<SSAInstruction> findEscapedVariablesBetween(CGNode node,
			ExecutionTrace trace, int branchStartIndex, int branchMergeIndex,
			boolean trueBranch) {
		List<SSAInstruction> escapedVars = new LinkedList<SSAInstruction>();
		Set<SSAInstruction> instrBetween = this.findInstructionsBetween(node, trace,
		    		branchStartIndex, branchMergeIndex, trueBranch);
		for(SSAInstruction ssa : instrBetween) {
			if(this.isEscapedVariable(node, ssa)) {
				escapedVars.add(ssa);
			}
		}
		return escapedVars;
		
	}
	
	public Set<SSAInstruction> findInstructionsBetween(CGNode node,
			ExecutionTrace trace, int branchStartIndex, int branchMergeIndex,
			boolean trueBranch) {
		List<InstructionExecChuck> lists
		    =  trace.getInstructions(node.getMethod().getSignature());
		
		Set<SSAInstruction> ssaSet = new LinkedHashSet<SSAInstruction>();
		
		for(InstructionExecChuck chuck : lists) {
			Utils.checkTrue(chuck.getInstructions(node, branchStartIndex).size() == 1);
			Utils.checkTrue(chuck.getInstructions(node, branchMergeIndex).size() == 1);
			if(chuck.evaluateToTrue(node, branchStartIndex) != trueBranch) {
				continue;
			} else {
				//fetch the instruction between
				List<SSAInstruction> ssaList = chuck.getInstructions(node, branchStartIndex, branchMergeIndex);
				ssaSet.addAll(ssaList);
			}
		}
		
		return ssaSet;
	}
	
	public boolean isEscapedVariable(CGNode node, SSAInstruction ssa) {
		throw new Error("Not implemented.");
	}
	
	public Statement createSliceCriteriaStatement(CGNode node, int ssaIndex) {
		Utils.checkTrue(ssaIndex >= 0 && ssaIndex < node.getIR().getInstructions().length,
				"index is illegal: " + ssaIndex + ", for: " + node);
		SSAInstruction instruction = WALAUtils.getInstruction(node, ssaIndex);
		Utils.checkTrue(this.isValidSliceCriteria(instruction), "not valid: " + instruction);
		return new NormalStatement(node, ssaIndex);
	}
	
	private boolean isValidSliceCriteria(SSAInstruction ssa) {
		return ssa instanceof SSAPutInstruction; //FIXME more check
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
