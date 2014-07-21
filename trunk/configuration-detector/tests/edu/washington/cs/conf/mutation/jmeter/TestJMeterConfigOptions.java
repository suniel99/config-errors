package edu.washington.cs.conf.mutation.jmeter;

import java.util.List;

import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import junit.framework.TestCase;

public class TestJMeterConfigOptions extends TestCase {

	static String config_file_1 = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\jmeter.properties";
	
	static String config_file_2 = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\saveservice.properties";
	
	static String config_file_3 = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\system.properties";
	
	public void testSampleConfigFiles() {
		ConfMutator mutator = new ConfMutator(config_file_1);
		List<MutatedConf> confs = mutator.mutateConfFile();
		System.out.println("# of mutated confs: " + confs.size());
		for(MutatedConf conf : confs) {
			System.out.println(conf.getMutatedConfOption() + ", " + conf.getMutatedConfValue() + ", orig value: " + conf.getOriginalValue());
			conf.writeToFile("./tmp-output-folder/tmp.properties");
			break;
		}
	}
	
}
