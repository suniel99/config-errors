package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.instrument.evol.TraceParser;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class ExecutionTraceReader {
	
	//a sample line
	//NORMAL:randoop.util.Reflection.isVisible(Ljava/lang/Class;)Z##11
	public static InstructionExecInfo createInstructionExecInfo(String line) {
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
	
	public static List<InstructionExecInfo> createInstructionExecInfoList(String traceFileName,
			String mapFileName) {
		Map<Integer, String> sigMap = TraceParser.parseSigNumMapping(mapFileName);
		List<InstructionExecInfo> list = new LinkedList<InstructionExecInfo>();
		List<String> fileContent = Files.readWholeNoExp(traceFileName);
		for(String line : fileContent) {
			if(line.trim().isEmpty()) {
				continue;
			}
			//convert the line into the real instruction
			Integer num = Integer.parseInt(line);
			String instrStr = sigMap.get(num);
			Utils.checkNotNull(instrStr);
			//the above code snippet is the only difference from the below method
			InstructionExecInfo execInfo = createInstructionExecInfo(instrStr);
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