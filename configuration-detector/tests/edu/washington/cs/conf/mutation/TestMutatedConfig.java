package edu.washington.cs.conf.mutation;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class TestMutatedConfig extends TestCase {

	public void testSampleConfig() {
		Map<String, String> options = new LinkedHashMap<String, String>();
		options.put("p", "2");
		options.put("t", "./subjects/weka/weather.arff");
		options.put("foo", "foo_mutated");
		
		Map<String, String> baseOptions = new LinkedHashMap<String, String>();
		//"-p", "2",  "-t", "./subjects/weka/weather.arff"
		baseOptions.put("p", "222");
		
		Set<String> onOffOptions = new LinkedHashSet<String>();
		
		String mutatedConf = "foo";
		String originalConfValue = "foo_original";
		
		MutatedConf conf = new MutatedConf(options, onOffOptions, mutatedConf, originalConfValue);
		
		System.out.println(Arrays.asList(conf.createCmdLineAsArgs()));
		assertEquals(Arrays.asList(conf.createCmdLineAsArgs()).toString(), "[-p, 2, -t, ./subjects/weka/weather.arff, -foo, foo_mutated]");
		
		System.out.println(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)));
		assertEquals(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)).toString(), "[-foo, foo_mutated, -p, 222]");
		
		baseOptions.clear();
		baseOptions.put("bar", "bar_value");
		System.out.println(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)));
		assertEquals(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)).toString(), "[-foo, foo_mutated, -bar, bar_value]");
		
		baseOptions.clear();
		baseOptions.put("foo", "new_mutated_foo_value");
		System.out.println(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)));
		assertEquals(Arrays.asList(conf.createCmdLinesAsArgs(baseOptions)).toString(), "[-foo, new_mutated_foo_value]");
	}
	
}
