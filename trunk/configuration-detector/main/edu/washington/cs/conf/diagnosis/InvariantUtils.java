package edu.washington.cs.conf.diagnosis;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import plume.UtilMDE;

import daikon.FileIO;
import daikon.Ppt;
import daikon.PptMap;
import daikon.PptTopLevel;
import daikon.diff.DetailedStatisticsVisitor;
import daikon.diff.Diff;
import daikon.diff.InvMap;
import daikon.diff.InvNode;
import daikon.diff.PrintAllVisitor;
import daikon.diff.RootNode;
import daikon.inv.Invariant;
import edu.washington.cs.conf.util.Utils;

public class InvariantUtils {
	
	public static String translateDaikonMethodSignatureToWALAs(String methodSig) {
		//e.g., DataStructures.StackArTester.push_noobserve(int)
		//      DataStructures.StackAr.push(java.lang.Object)
		//to: randoop.main.GenTests.handle([Ljava/lang/String;)Z_index_91
	    //   or. randoop.util.MultiMap.add(Ljava/lang/Object;Ljava/lang/Object;)V
		return methodSig;
	}
	
	public static boolean stringEquals(String daikonMethod, String jvmMethod) {
		Utils.checkTrue(daikonMethod.endsWith(")"), "illegal daikon method: " + daikonMethod);
		Utils.checkTrue(!jvmMethod.endsWith(")"), "illegal jvm method: " + jvmMethod);
		
		int daikonLP = daikonMethod.indexOf("(");
		int daikonRP = daikonMethod.indexOf(")");
		
		int jvmLP = jvmMethod.indexOf("(");
		int jvmRP = jvmMethod.indexOf(")");
		
		//ignore the return type of jvm method
		jvmMethod = jvmMethod.substring(0, jvmRP + 1);
		
		Utils.checkTrue(daikonLP != -1 && daikonRP != -1 && jvmLP != -1 && jvmRP != -1
				&& daikonRP > daikonLP && jvmRP > jvmLP, "input are not legal: " + daikonMethod + ", "
				+ jvmMethod);
		
		String daikonMethodName = daikonMethod.substring(0, daikonLP);
		String jvmMethodName = jvmMethod.substring(0, jvmLP);
		
		if(!daikonMethodName.equals(jvmMethodName)) {
			return false;
		}
		
		String daikonArgs = daikonMethod.substring(daikonLP + 1, daikonRP);
		String jvmArgs = jvmMethod.substring(jvmLP + 1, jvmRP);
		
		//FIXME
		//need to deal with arrays, object, and primitive types
		
		return true;
	}

	public static Set<String> fetchMethodsWithDiffInvariants(String filename1, String filename2) throws Exception {
		Diff diff = new Diff(false, false);
		Comparator<Invariant> defaultComparator = new Invariant.ClassVarnameComparator();
		// all null
		diff.setInvSortComparator1(selectComparator(null, defaultComparator));
		diff.setInvSortComparator2(selectComparator(null, defaultComparator));
		diff.setInvPairComparator(selectComparator(null, defaultComparator));

		InvMap invMap1 = readInvMap(new File(filename1));
		InvMap invMap2 = readInvMap(new File(filename2));
		RootNode root = diff.diffInvMap(invMap1, invMap2, true);

		PrintDifferingInvariantsVisitor v = new PrintDifferingInvariantsVisitor(
				System.out, false, false, false);
		root.accept(v);

		//get the affected points and then sanitize
		Set<String> ppts = v.getAffectedPoints();
		Set<String> methodNames = extractMethods(ppts);
		
		return methodNames;
	}
	
	static Set<String> extractMethods(Set<String> ppts) {
		Set<String> methods = new LinkedHashSet<String>();
		//<DataStructures.StackArTester.push_noobserve(int):::EXIT>
		//<DataStructures.StackArTester.push_noobserve(int):::ENTER>
		for(String ppt : ppts) {
//			System.out.println("----------" + ppt);
			Utils.checkTrue((ppt.endsWith(":::EXIT") ||ppt.endsWith(":::ENTER") ),
					"Wrong format: " + ppt);
			String method = ppt.substring(0, ppt.indexOf(":::"));
			methods.add(method);
		}
		return methods;
	}

	/***
	 * All Daikon-specific utility methods below, most of which is handling
	 * Daikon's internal structure.
	 * */
	static Comparator<Invariant> selectComparator(String classname,
			Comparator<Invariant> defaultComparator)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		if (classname != null) {
			Class<?> cls = Class.forName(classname);
			@SuppressWarnings("unchecked")
			Comparator<Invariant> cmp = (Comparator<Invariant>) cls.newInstance();
			return cmp;
		} else {
			return defaultComparator;
		}
	}

	static InvMap readInvMap(File file) throws IOException,
			ClassNotFoundException {
		Object o = UtilMDE.readObject(file);
		if (o instanceof InvMap) {
			return (InvMap) o;
		} else {
			PptMap pptMap = FileIO.read_serialized_pptmap(file, false);
			return convertToInvMap(pptMap);
		}
	}

	static final Comparator<PptTopLevel> PPT_COMPARATOR = new Ppt.NameComparator();
	static InvMap convertToInvMap(PptMap pptMap) {
		InvMap map = new InvMap();

		SortedSet<PptTopLevel> ppts = new TreeSet<PptTopLevel>(PPT_COMPARATOR);
		ppts.addAll(pptMap.asCollection());

		for (PptTopLevel ppt : ppts) {
			List<Invariant> invs = UtilMDE.sortList(ppt.getInvariants(),
					PptTopLevel.icfp);
			map.put(ppt, invs);
		}
		return map;
	}
}

class PrintDifferingInvariantsVisitor extends PrintAllVisitor {
	
	private Set<String> pptWithDiffInvariants = new LinkedHashSet<String>();

	public static final Logger debug = Logger
			.getLogger("daikon.diff.DetailedStatisticsVisitor");
	private boolean printUninteresting;

	public PrintDifferingInvariantsVisitor(PrintStream ps, boolean verbose,
			boolean printEmptyPpts, boolean printUninteresting) {
		super(ps, verbose, printEmptyPpts);
		this.printUninteresting = printUninteresting;
	}
	
	public Set<String> getAffectedPoints() {
		return this.pptWithDiffInvariants;
	}

	public void visit(InvNode node) {
		Invariant inv1 = node.getInv1();
		Invariant inv2 = node.getInv2();
		if (shouldPrint(inv1, inv2)) {
			// System.err.println(inv1);
			if (inv1 != null && inv1.ppt != null && inv1.ppt.parent != null) {
//				System.err.println(inv1.ppt.parent.name);
				pptWithDiffInvariants.add(inv1.ppt.parent.name);
			}
			if (inv2 != null && inv2.ppt != null && inv2.ppt.parent != null) {
//				System.err.println(inv2.ppt.parent.name);
				pptWithDiffInvariants.add(inv2.ppt.parent.name);
			}
			super.visit(node);
		}
	}

	/**
	 * Returns true if the pair of invariants should be printed, depending on
	 * their type, relationship, and printability.
	 **/
	protected boolean shouldPrint(/* @Nullable */Invariant inv1, /* @Nullable */
			Invariant inv2) {
		int type = DetailedStatisticsVisitor.determineType(inv1, inv2);
		if (type == DetailedStatisticsVisitor.TYPE_NULLARY_UNINTERESTING
				|| type == DetailedStatisticsVisitor.TYPE_UNARY_UNINTERESTING) {
			return printUninteresting;
		}

		int rel = DetailedStatisticsVisitor.determineRelationship(inv1, inv2);
		if (rel == DetailedStatisticsVisitor.REL_SAME_JUST1_JUST2
				|| rel == DetailedStatisticsVisitor.REL_SAME_UNJUST1_UNJUST2
				|| rel == DetailedStatisticsVisitor.REL_DIFF_UNJUST1_UNJUST2
				|| rel == DetailedStatisticsVisitor.REL_MISS_UNJUST1
				|| rel == DetailedStatisticsVisitor.REL_MISS_UNJUST2) {
			if (debug.isLoggable(Level.FINE)) {
				debug.fine(" Returning false");
			}

			return false;
		}

		if ((inv1 == null || !inv1.isWorthPrinting())
				&& (inv2 == null || !inv2.isWorthPrinting())) {
			if (debug.isLoggable(Level.FINE)) {
				debug.fine(" Returning false");
			}
			return false;
		}

		if (debug.isLoggable(Level.FINE)) {
			debug.fine(" Returning true");
		}

		return true;
	}
}