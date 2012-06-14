package edu.washington.cs.conf.experiments.daikon;

import java.util.Set;

import edu.washington.cs.conf.diagnosis.InvariantUtils;
import junit.framework.TestCase;

public class TestInvariantDiffInArStack extends TestCase {
	
	public void testArStack() throws Exception {
		String filename1 = "D:\\research\\configurations\\daikon\\bin\\java-examples\\StackAr\\StackArTester.inv.gz";
		String filename2 = "D:\\research\\configurations\\daikon\\bin\\java-examples\\StackAr\\StackArTester.inv-backup.gz";
		Set<String> sets = InvariantUtils.fetchMethodsWithDiffInvariants(filename1, filename2);
		
		System.out.println(sets);
		assertEquals(3, sets.size());
		assertEquals("[DataStructures.StackArTester.createItem(int), DataStructures.StackArTester.push(int), DataStructures.StackArTester.push_noobserve(int)]",
				sets.toString());
	}

}
