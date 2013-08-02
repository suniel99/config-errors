package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class ExecutionTraceReader {
	
	static Map<String, InstructionExecInfo> cachedExec = new LinkedHashMap<String, InstructionExecInfo>();
	
	//a sample line
	//NORMAL:randoop.util.Reflection.isVisible(Ljava/lang/Class;)Z##11
	public static InstructionExecInfo createInstructionExecInfo(String line) {
		if(cachedExec.containsKey(line)) {
			return cachedExec.get(line);
		} else {
			InstructionExecInfo exec = createInstructionExecInfo_internal(line);
			cachedExec.put(line, exec);
			return exec;
		}
		
	}
	
	private static InstructionExecInfo createInstructionExecInfo_internal(String line) {
		InstructionExecInfo execInfo = null;
		String[] splits = line.split(EfficientTracer.SEP);
		Utils.checkTrue(splits.length == 2);
		Integer index = Integer.parseInt(splits[1]);
		String other = splits[0];
		if(other.startsWith(EfficientTracer.NORMAL)) {
			String context = other.substring(EfficientTracer.NORMAL.length());
			execInfo = new InstructionExecInfo(context, index);
		} else if (other.startsWith(EfficientTracer.EVAL)) {
			String context = other.substring(EfficientTracer.EVAL.length());
			execInfo = new BranchInstructionExecInfo(context, index, true);
		} else if (other.startsWith(EfficientTracer.EXEC)) {
			String context = other.substring(EfficientTracer.EXEC.length());
			execInfo = new BranchInstructionExecInfo(context, index, false);
		} else {
			throw new Error("Invalid: " + line);
		}
		return execInfo;
	}
	
	//line looks like:
	//NORMAL:randoop.util.Reflection.isVisible(Ljava/lang/Class;)Z##11
	public static boolean checkInstruction(String line, String methodSig, int index) {
		String instrSig = methodSig + EfficientTracer.SEP + index;
		return line.trim().endsWith(instrSig);
	}
	
	public static List<InstructionExecInfo> createInstructionExecInfo(String traceFileName,
			String mapFileName) {
		return createPredicateExecInfoInTrace_internal(traceFileName, mapFileName, false);
	}
	
	public static List<InstructionExecInfo> createPredicateExecInfoInTrace(String traceFileName,
			String mapFileName) {
		return createPredicateExecInfoInTrace_internal(traceFileName, mapFileName, true);
	}
	
	private static List<InstructionExecInfo> createPredicateExecInfoInTrace_internal(String traceFileName,
			String mapFileName, boolean onlyPredicate) {
		Map<Integer, String> sigMap = SigMapParser.parseSigNumMapping(mapFileName);
		List<InstructionExecInfo> list = new LinkedList<InstructionExecInfo>();
		List<String> fileContent = Files.readWholeNoExp(traceFileName);
		for(String line : fileContent) {
			if(line.trim().isEmpty()) {
				continue;
			}
			if(line.equals("null")) { //not sure why there is a null in trace file
				continue;
			}
			//just create predicate
			if(onlyPredicate) {
			    if(line.startsWith(EfficientTracer.NORMAL)) {
				    continue;
			    }
			}
			String[] splits = line.split(EfficientTracer.EVAL_SEP);
			Utils.checkTrue(splits.length == 2, "line is: " + line);
			
			//convert the line into the real instruction
			Integer num = Integer.parseInt(splits[1]);
			String instrStr = sigMap.get(num);
			Utils.checkNotNull(instrStr);
			
			String newLine = splits[0] + EfficientTracer.EVAL_SEP + instrStr;
			//the above code snippet is the only difference from the below method
			InstructionExecInfo execInfo = createInstructionExecInfo(newLine);
			list.add(execInfo);
		}
		return list;
	}
	
	public static List<InstructionExecInfo> createInstructionExecInfoList(String fileName) {
		List<InstructionExecInfo> list = new LinkedList<InstructionExecInfo>();
		List<String> fileContent = Files.readWholeNoExp(fileName);
		for(String line : fileContent) {
			if(line.trim().isEmpty()) {
				continue;
			}
			InstructionExecInfo execInfo = createInstructionExecInfo(line);
			list.add(execInfo);
		}
		return list;
	}

	//read predicate exec info using the sigmap
	//in the predicate trace file, it records:
	//  21938==3:2
	//in the sigmap, it records:
	//  full_instruction_signature##16=>21938 (the predicate id)
	public static Collection<PredicateExecInfo>
	    createPredicateExecInfoList(String predicateFileName, String mapFileName) {
		Map<Integer, String> sigMap = SigMapParser.parseSigNumMapping(mapFileName);
		//then parse the trace file
		Collection<PredicateExecInfo> predicates = new LinkedList<PredicateExecInfo>();
		List<String> fileContent = Files.readWholeNoExp(predicateFileName);
		for(String line : fileContent) {
//			System.out.println(".");
			if(line.trim().isEmpty()) {
				continue;
			}
			String[] splits = line.split(EfficientTracer.PRED_SEP);
			Utils.checkTrue(splits.length == 2);
			Integer instrIndex = Integer.parseInt(splits[0]);
			String instruction = sigMap.get(instrIndex);
			if(instruction == null) {
				System.err.println("Error: " + line);
				Utils.fail("Error: " + line);
				continue;
			}
			String newLine = instruction + EfficientTracer.PRED_SEP + splits[1];
			PredicateExecInfo pred = createPredicateExecInfo(newLine);
			predicates.add(pred);
		}
		return predicates;
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
	
}