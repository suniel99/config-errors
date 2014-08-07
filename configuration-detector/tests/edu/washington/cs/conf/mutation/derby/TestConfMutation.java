package edu.washington.cs.conf.mutation.derby;

import java.util.List;

import edu.washington.cs.conf.mutation.ConfFileParser;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import junit.framework.TestCase;

public class TestConfMutation extends TestCase {

	public void testParseConfig() {
		String filePath = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor\\derby.properties";
		ConfFileParser parser = new ConfFileParser(filePath);
		parser.parse();
		parser.dumpFile();
		System.out.println(parser.getConfOptionNames());
		System.out.println(parser.getConfOptionValues());
		
		//mutate it
		ConfMutator mutator = new ConfMutator(filePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		System.out.println("Number of mutations: " + mutates.size());
		
		for(MutatedConf mutate : mutates) {
			System.out.println(mutate.getMutatedContent());
		}
	}
}
