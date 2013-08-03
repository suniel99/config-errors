package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;

public class EvolConfOptionRepository {
	
	private static final String optionDir = "./tests/edu/washington/cs/conf/analysis/evol/experiments/";

	public static final String randoopOldOptionFile = optionDir + "randoop-old-options.txt";
	public static ConfEntityRepository randoopOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(randoopOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String randoopNewOptionFile = optionDir + "randoop-new-options.txt";
	public static ConfEntityRepository randoopNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(randoopNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String wekaOldOptionFile = optionDir + "weka-old-options.txt";
	public static ConfEntityRepository wekaOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(wekaOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String wekaNewOptionFile = optionDir + "weka-new-options.txt";
	public static ConfEntityRepository wekaNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(wekaNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String synopticOldOptionFile = optionDir + "synoptic-old-options.txt";
	public static ConfEntityRepository synopticOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(synopticOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String synopticNewOptionFile = optionDir + "synoptic-new-options.txt";
	public static ConfEntityRepository synopticNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(synopticNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String jmeterOldOptionFile = optionDir + "jmeter-old-options.txt";
	public static ConfEntityRepository jmeterOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(jmeterOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String jmeterNewOptionFile = optionDir + "jmeter-new-options.txt";
	public static ConfEntityRepository jmeterNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(jmeterNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static ConfEntity[] jchordOldConfs() {
		return new ConfEntity[]{};	
	}
	
	public static ConfEntity[] jchordNewConfs() {
		return new ConfEntity[]{};
	}
}