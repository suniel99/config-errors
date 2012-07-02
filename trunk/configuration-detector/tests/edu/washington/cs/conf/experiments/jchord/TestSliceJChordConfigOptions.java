package edu.washington.cs.conf.experiments.jchord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestSliceJChordConfigOptions extends TestCase {
	public static String jchord_instrument_file = "./jchord_option_instr_ser.dat";
	
	public static String jchord_instrument_txt = "./jchord_option_instr.txt";
	
	public static String jchord_main = "Lchord/project/Main";
	
	public static String jchord_exclusion = "ChordExclusions.txt";
	
	public void testInitAllConfigOptions() {
		String path = TestInstrumentJChord.jchord_notrace;
//		String mainClass = "Lchord/project/Main"
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		ConfEntityRepository repo = new ConfEntityRepository(jchordConfList);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : jchordConfList) {
			System.out.println(conf);
		}
		assertEquals(77, jchordConfList.size());
	}
	
	public void testSliceOptionsInJChordNoPrune() {
		sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
	}
	
	public void testSliceOptionsInJChordAndSeeInside() {
		Collection<ConfPropOutput> outputs = sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
		
		List<String> options = Arrays.asList("runBefore", "extraMethodsList", "instrSchemeFileName", "traceKind", "instrKind",
				"dlogAnalysisPathName", "javaAnalysisPathName", "printRels", "printClasses", "runAnalyses", "checkExcludeStr",
				"checkExtExcludeStr", "checkStdExcludeStr", "scopeExcludeStr", "scopeExtExcludeStr", "scopeStdExcludeStr",
				"CHkind", "runtimeJvmargs", "reflectKind", "runIDs", "srcPathName", "userClassPathName", "mainClassName");
		for(ConfPropOutput output : outputs) {
			if(options.contains(output.conf.getConfName())) {
				System.out.println("conf name: " + output.conf.getConfName());
				System.out.println("   contain chord.project.Config.<clinit>? : " + output.includeStatement("chord.project.Config.<clinit>", 39));
			}
		}
		
	}
	
	public void testSliceOptionsInJChordWithPrune() {
		sliceOptionsInJChord(ChordExpUtils.getChordConfList(), true);
	}
	
	public void testSliceSampleOptions() {
		sliceOptionsInJChord(ChordExpUtils.getSampleConfList(), false);
	}
	
	public static Collection<ConfPropOutput> getJChordConfOutputs() {
		return sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
	}
	
	static String path = TestInstrumentJChord.jchord_notrace;
	static String mainClass = jchord_main;
	static String exFile = jchord_exclusion;
	
	public static Collection<ConfPropOutput> getJChordConfOutputsFullSlice() {
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, jchordConfList, exFile, CG.OneCFA, false,
				DataDependenceOptions.FULL, ControlDependenceOptions.FULL);
		return confs;
	}
	
	public static Collection<ConfPropOutput> sliceOptionsInJChord(List<ConfEntity> jchordConfList, boolean prune) {
//		Log.logConfig("./jchord-config-slice.txt");
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, jchordConfList, exFile, prune);
//		Log.removeLogging();
		return confs;
	}
	
	public void testCreateInstrumentSchema() {
		Collection<ConfPropOutput> outputs = getJChordConfOutputs();
		//sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE); //NOTE use the abstraction of source predicate
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, jchord_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, jchord_instrument_txt);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(jchord_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
}
