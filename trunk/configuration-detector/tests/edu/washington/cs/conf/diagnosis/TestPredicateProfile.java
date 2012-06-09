package edu.washington.cs.conf.diagnosis;

import junit.framework.TestCase;

public class TestPredicateProfile extends TestCase {

	public void testImportanceValue() {
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				1, 1);
		PredicateProfile p2 = new PredicateProfile("option2", "context2",
				5, 4);
		PredicateProfile p21 = new PredicateProfile("option2", "context2",
				5, 3);
		PredicateProfile p3 = new PredicateProfile("option2", "context2",
				100, 90);
		
		System.out.println(p1.importanceValue());
		System.out.println(p2.importanceValue());
		System.out.println(p21.importanceValue());
		System.out.println(p3.importanceValue());
	}
	
	public void testImportanceValueDiff() {
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				9, 5);
		PredicateProfile p11 = new PredicateProfile("option2", "context2",
				5, 2);
		System.out.println(p1.importanceValue());
		System.out.println(p11.importanceValue());
		System.out.println(p11.importanceValue() - p1.importanceValue());
		
		System.out.println("------------------");
		
		PredicateProfile p2 = new PredicateProfile("option1", "context1",
				1, 1);
		PredicateProfile p20 = new PredicateProfile("option1", "context1",
				1, 0);
		System.out.println(p2.importanceValue());
		System.out.println(p20.importanceValue());
		System.out.println(p20.importanceValue() - p2.importanceValue());
		
		System.out.println("------------------");
		
		PredicateProfile p3 = new PredicateProfile("option1", "context1",
				10, 9);
		PredicateProfile p30 = new PredicateProfile("option1", "context1",
				9, 2);
		System.out.println(p3.importanceValue());
		System.out.println(p30.importanceValue());
		System.out.println(p30.importanceValue() - p3.importanceValue());
		
		System.out.println("------------------");
		
		PredicateProfile p4 = new PredicateProfile("option1", "context1",
				5, 5);
		PredicateProfile p40 = new PredicateProfile("option1", "context1",
				5, 0);
		System.out.println(p4.importanceValue());
		System.out.println(p40.importanceValue());
		System.out.println(p40.importanceValue() - p4.importanceValue());
		
		System.out.println("------------------");
	}
}
