package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.washington.cs.conf.diagnosis.PredicateProfile;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class ConfUtils {

	
	public static String sep = ";";
	
	public static Collection<ConfEntity> parseConfEntities(String fileName) {
		Collection<ConfEntity> all = new LinkedHashSet<ConfEntity>();
		List<String> lines = Files.readWholeNoExp(fileName);
		for(String line : lines) {
			if(line.trim().isEmpty()) {
				continue;
			}
			ConfEntity entity = parseConfEntity(line);
			if(all.contains(entity)) {
				System.err.println("Duplicated entity: " + entity);
			} else {
				all.add(entity);
			}
		}
		return all;
	}
	
	public static ConfEntity parseConfEntity(String line) {
		String[] splits = line.split(sep);
		Utils.checkTrue(splits.length == 4, "Incorrect length: " + splits.length);
		String className = splits[0].trim();
		String confName = splits[1].trim();
		String affMethod = splits[2].trim().isEmpty() ? null : splits[2].trim();
		boolean isStatic = Boolean.parseBoolean(splits[3].trim());
		return new ConfEntity(className, confName, affMethod, isStatic);
	}
	
	//all this method does is to set the field source line number / source text in each PredicateProfile
	public static void setUpLineNumberAndSource(String sourceDir,
			Collection<ConfPropOutput> propOutputs, Collection<PredicateProfile> profiles) {
		for(PredicateProfile profile : profiles) {
//			int srcLineNum = -1;
//			String srcText = "NOT_SET_UP_IN_ConfUtils";
			
			String configName = profile.getConfigFullName();
			ConfPropOutput output = findConfPropOutputByConfName(propOutputs, configName);
			
			if(output == null) {
				continue;
			}
			
			Utils.checkNotNull(output, "configName: " + configName);
			
			IRStatement irs = output.getStatement(profile.getMethodSig(), profile.getInstructionIndex());
			
			if(irs == null) {
				profile.setSourceLineNumber(-1);
				profile.setPredicateInSource("Unavailable from source: " + profile.getMethodSig() + ", " + profile.getInstructionIndex());
				continue;
			}
			
			Utils.checkNotNull(irs, "profile: " + profile.getMethodSig());
			String fullClassName = irs.getDeclaringFullClassName();
			
			int srcLineNum = irs.getLineNumber();
			String srcText = Files.fetchLineInFile(sourceDir, fullClassName, srcLineNum);
			
			profile.setSourceLineNumber(srcLineNum);
			profile.setPredicateInSource(srcText);
		}
	}
	
	public static ConfPropOutput findConfPropOutputByConfName(Collection<ConfPropOutput> propOutputs,
			String fullConfigName) {
		for(ConfPropOutput output : propOutputs) {
			if(output.getConfEntity().getFullConfName().equals(fullConfigName)) {
				return output;
			}
		}
		return null;
	}
	
	/**
	 * FIXME
	 * may not be a right place
	 * */
	public static String extractFullClassName(String methodSig) {
		return methodSig.substring(0, methodSig.lastIndexOf("."));
	}
	
	/**
	 * Removes all statements corresponding to the same statement
	 * */
	public static Set<IRStatement> removeSameStmtsInDiffContexts(Set<IRStatement> stmts) {
		Set<String> existed = new LinkedHashSet<String>();
		Set<IRStatement> filtered = new LinkedHashSet<IRStatement>();
		for(IRStatement stmt : stmts) {
			String sig = stmt.getUniqueSignature(); //method name + instruction string + instruction index
			if(existed.contains(sig)) {
				continue;
			}
			existed.add(sig);
			filtered.add(stmt);
		}
		return filtered;
	}
}