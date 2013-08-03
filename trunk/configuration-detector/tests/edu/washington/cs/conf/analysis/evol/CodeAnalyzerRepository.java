package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.cha.ClassHierarchy;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.WALAUtils;

public class CodeAnalyzerRepository {

	//for the randoop case
	public static final String randoop121Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
	public static CodeAnalyzer getRandoop121Analyzer() {
		String randoopMain = "Lrandoop/main/Main";
		
		CodeAnalyzer coder121 = new CodeAnalyzer(randoop121Path, randoopMain);
		coder121.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder121.slicer.setCGType(CG.ZeroCFA);
		
		return coder121;
	}
	
	public static final String randoop132Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\plume.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\jakarta-oro-2.0.8.jar";
	public static CodeAnalyzer getRandoop132Analyzer() {
		String randoopMain = "Lrandoop/main/Main";
		
		CodeAnalyzer coder132 = new CodeAnalyzer(randoop132Path, randoopMain);
		coder132.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder132.slicer.setCGType(CG.ZeroCFA);
		
		return coder132;
	}
	
	static String synopticMainClass = "Lsynoptic/main/Main";
	public static final String oldSynopticPath = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\lib\\plume.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\lib\\junit-4.9b2.jar";
	
	public static CodeAnalyzer getSynopticOldAnalyzer() {
		CodeAnalyzer oldCoder = new CodeAnalyzer(oldSynopticPath, synopticMainClass);
		oldCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		oldCoder.slicer.setCGType(CG.ZeroCFA);
		
		return oldCoder;
	}
	
	public static final String newSynopticPath = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\plume.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\junit-4.9b2.jar";
	public static CodeAnalyzer getSynopticNewAnalyzer() {
		CodeAnalyzer newCoder = new CodeAnalyzer(newSynopticPath, synopticMainClass);
		newCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		newCoder.slicer.setCGType(CG.ZeroCFA);
		
		return newCoder;
	}
	
	static String wekaMainClass = "Lweka/classifiers/trees/J48";
	public final static String oldWekaPath = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka.jar";
	public static CodeAnalyzer getWekaOldAnalyzer() {
		CodeAnalyzer oldCoder = new CodeAnalyzer(oldWekaPath, wekaMainClass);
		oldCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		oldCoder.slicer.setCGType(CG.ZeroCFA);
		return oldCoder;
	}
	
	public final static String newWekaPath = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka.jar";
	public static CodeAnalyzer getWekaNewAnalyzer() {
		CodeAnalyzer newCoder = new CodeAnalyzer(newWekaPath, wekaMainClass);
		newCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		newCoder.slicer.setCGType(CG.ZeroCFA);
		
		return newCoder;
	}
	
	static String jmeterStartClass = "org.apache.jmeter.JMeter";
	static String jmeterMainClassSig = "Lorg/apache/jmeter/NewDriver";
	static String jmeterStartMethod = "start";
	
	//void start(String[] args)
	public static CodeAnalyzer getJMeterOldAnalyzer() {
		String classPath = getOldJMeterPath();
		
		CodeAnalyzer oldAnalyzer = new CodeAnalyzer(classPath, jmeterMainClassSig);
		oldAnalyzer.slicer.setExclusionFile("JavaAllExclusions.txt");
		oldAnalyzer.slicer.setCGType(CG.RTA);
		
		//must customize the entry points
		oldAnalyzer.slicer.buildClassHierarchy();
		ClassHierarchy cha = oldAnalyzer.slicer.getClassHierarchy();
		Iterable<Entrypoint> points = WALAUtils.createEntrypoints(jmeterStartClass, jmeterStartMethod, cha);
		oldAnalyzer.slicer.setEntrypoints(points);
		
		return oldAnalyzer;
	}
	
	public static CodeAnalyzer getJMeterNewAnalyzer() {
		String classPath = getNewJMeterPath();
		
		CodeAnalyzer newAnalyzer = new CodeAnalyzer(classPath, jmeterMainClassSig);
		newAnalyzer.slicer.setExclusionFile("JavaAllExclusions.txt");
		newAnalyzer.slicer.setCGType(CG.RTA);
		
		//must customize the entry points
		newAnalyzer.slicer.buildClassHierarchy();
		ClassHierarchy cha = newAnalyzer.slicer.getClassHierarchy();
		Iterable<Entrypoint> points = WALAUtils.createEntrypoints(jmeterStartClass, jmeterStartMethod, cha);
		newAnalyzer.slicer.setEntrypoints(points);
		
		return newAnalyzer;
	}
	
	static String chordMainClass = "Lchord/project/Main";
	static String chordExclusions = "ChordExclusions.txt";
	public static CodeAnalyzer getJChordOldAnalyzer() {
		String oldChordPath = "D:\\research\\confevol\\subject-programs\\jchord\\chord-bin-2.0\\chord.jar";
		CodeAnalyzer oldCoder = new CodeAnalyzer(oldChordPath, chordMainClass);
		oldCoder.slicer.setExclusionFile(chordExclusions);
		oldCoder.slicer.setCGType(CG.ZeroCFA);
		return oldCoder;
	}
	
	public static CodeAnalyzer getJChordNewAnalyzer() {
		String newChordPath = "D:\\research\\confevol\\subject-programs\\jchord\\chord-bin-2.1\\chord.jar";
		CodeAnalyzer newCoder = new CodeAnalyzer(newChordPath, chordMainClass);
		newCoder.slicer.setExclusionFile(chordExclusions);
		newCoder.slicer.setCGType(CG.ZeroCFA);
		return newCoder;
	}
	
	//the long classpath for JMeter
	public static String getOldJMeterPath() {
		String startJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\bin\\ApacheJMeter.jar";
		
		String allJars = 
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\activation-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\avalon-framework-4.1.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bsf-2.4.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bsf-api-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bsh-2.0b5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bshclient.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-codec-1.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-collections-3.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-httpclient-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-io-2.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-jexl-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-jexl-2.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-lang-2.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-lang3-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-logging-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-net-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-datasource-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-instrument-1.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-logger-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-pool-1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_components.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_ftp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_functions.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_http.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_java.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_jdbc.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_jms.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_junit.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_ldap.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_mail.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_monitors.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_native.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_report.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_tcp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\evoltracer.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\original\\ApacheJMeter_core.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\geronimo-jms_1.1_spec-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\htmllexer-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\htmlparser-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\httpclient-4.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\httpcore-4.2.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\httpmime-4.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jcharts-0.7.5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jdom-1.1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jorphan.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jtidy-r938.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\junit-4.10.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\junit\\test.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\logkit-2.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\mail-1.4.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\oro-2.0.8.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\rhino-1.7R3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\serializer-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\soap-2.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xalan-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xercesImpl-2.9.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xml-apis-1.3.04.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xmlgraphics-commons-1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xmlpull-1.1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xpp3_min-1.1.4c.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xstream-1.4.2.jar";

		
		return allJars + Globals.pathSep + startJar;
	}
	public static String getNewJMeterPath() {
		String allJars =
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\activation-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\avalon-framework-4.1.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\bsf-2.4.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\bsh-2.0b5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\bshclient.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-codec-1.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-collections-3.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-httpclient-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-io-2.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-jexl-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-jexl-2.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-lang-2.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-lang3-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-logging-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-net-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-datasource-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-instrument-1.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-logger-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-pool-1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_components.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_ftp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_functions.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_http.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_java.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_jdbc.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_jms.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_junit.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_ldap.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_mail.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_monitors.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_native.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_report.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_tcp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\evoltracer.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\original\\ApacheJMeter_core.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\geronimo-jms_1.1_spec-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\htmllexer-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\htmlparser-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\httpclient-4.2.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\httpcore-4.2.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\httpmime-4.2.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jcharts-0.7.5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jdom-1.1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jodd-core-3.4.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jodd-lagarto-3.4.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jorphan.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jsoup-1.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jtidy-r938.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\junit-4.10.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\junit\\test.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\logkit-2.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\mail-1.4.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\oro-2.0.8.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\rhino-1.7R4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\serializer-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\slf4j-api-1.7.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\soap-2.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\tika-core-1.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\tika-parsers-1.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xalan-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xercesImpl-2.9.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xml-apis-1.3.04.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xmlgraphics-commons-1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xmlpull-1.1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xpp3_min-1.1.4c.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xstream-1.4.2.jar";

		return allJars;
	}
}