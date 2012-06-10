package edu.washington.cs.conf.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TestUtils extends TestCase {

	public void testMapRank() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 5);
		map.put("c", 3);
		map.put("d", 2);
		List<String> sortedKeys = Utils.sortByValueAndReturnKeys(map, false);
		System.out.println(sortedKeys);
		assertEquals("[b, c, d, a]", sortedKeys.toString());
	}
	
	public void testAverage() {
		Collection<Integer> fs = new LinkedList<Integer>();
		fs.add(4);
		fs.add(3);
		fs.add(5);
		fs.add(6);
		fs.add(2);
		System.out.println(Utils.average(fs));
		assertEquals(4.0f, Utils.average(fs));
	}
	
}
