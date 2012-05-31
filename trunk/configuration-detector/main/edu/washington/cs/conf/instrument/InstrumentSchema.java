package edu.washington.cs.conf.instrument;

import instrument.Globals;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ShrikePoint;
import edu.washington.cs.conf.util.Utils;

/**
 * A class representing how to instrument a program
 * */
public class InstrumentSchema {

	public enum TYPE{PREDICATE, ALL, SOURCE_PREDICATE}
	
	private final Map<ConfEntity, Collection<ShrikePoint>> locations
	    = new LinkedHashMap<ConfEntity, Collection<ShrikePoint>>();
	
	private Map<String, Map<String, Set<Integer>>> internals = null;
	
	private TYPE type = TYPE.PREDICATE;
	
	public void addInstrumentationPoint(Collection<ConfPropOutput> confOutputs) {
		System.err.println("Using location types: " + type);
		for(ConfPropOutput output : confOutputs) {
			ConfEntity entity = output.getConfEntity();
			Collection<ShrikePoint> points = null;
			if(type.equals(TYPE.PREDICATE)) {
			    points = output.getNumberedBranchShrikePoints();
			} else if(type.equals(TYPE.ALL)) {
				points = output.getNumberedShrikePoints();
			} else if (type.equals(TYPE.SOURCE_PREDICATE)) {
				points = output.getNumberedBranchShrikePointsInSource();
			}
			Utils.checkNotNull(points);
			Utils.checkTrue(!locations.containsKey(entity));
			locations.put(entity, points);
		}
		restore();
	}
	
	//this is only for experimentation and debugging purpose.
	public void addInstrumentationPoint(ConfEntity entity, Collection<ShrikePoint> pts) {
		Utils.checkTrue(!locations.containsKey(entity));
		locations.put(entity, pts);
		//set the internals to null
		restore();
	}
	
	public void setType(TYPE t) {
		this.type = t;
	}
	
	private boolean needRebuild() {
		return internals == null;
	}
	
	private void restore() {
		internals = null;
	}
	
	/**
	 * Given a method Sig, return (conf <-> predicates (instruction index))
	 * */
	public Map<String, Set<Integer>> getInstrumentationPoints(String methodSig) {
		if(needRebuild()) {
			buildInternals();
		}
		Map<String, Set<Integer>> ret = Collections.EMPTY_MAP;
		if(internals.containsKey(methodSig)) {
			ret = internals.get(methodSig);
		}
		return ret;
	}
	
	public Set<String> getInstrumentationPredicates(String methodSig, int instrIndex) {
		Set<String> predicates = new LinkedHashSet<String>();
		Map<String, Set<Integer>>  points = getInstrumentationPoints(methodSig);
		
		for(String predicate : points.keySet()) {
			if(points.get(predicate).contains(instrIndex)) {
				predicates.add(predicate);
			}
		}
		
		return predicates;
	}
	
	public void saveToFileAsText(Writer out) throws IOException {
		for(ConfEntity entity : this.locations.keySet()) {
			out.write(entity.toString());
			out.write(Globals.lineSep);
			out.write("  Num of locations: " + this.locations.get(entity).size());
			out.write(Globals.lineSep);
			for(ShrikePoint p : this.locations.get(entity)) {
				out.write("     ");
				out.write(p.toString());
				out.write("   ->  ");
				out.write(p.getInstructionStr());
				out.write(Globals.lineSep);
			}
		}
	}
	
	public void writeToFile(ObjectOutputStream  out) throws IOException {
		int mapEntryNum = this.locations.size();
		out.writeInt(mapEntryNum);
		for(ConfEntity conf : this.locations.keySet()) {
			int shrikePointNum = this.locations.get(conf).size();
			out.writeObject(conf);
			out.writeInt(shrikePointNum);
			for(ShrikePoint p : this.locations.get(conf)) {
				out.writeObject(p);
			}
		}
	}
	
	public static InstrumentSchema readFromFile(ObjectInputStream  in) throws IOException, ClassNotFoundException {
		InstrumentSchema schema = new InstrumentSchema();
		int mapEntryNum = in.readInt();
		for(int i = 0; i < mapEntryNum; i++) {
			ConfEntity entity = (ConfEntity)in.readObject();
			int shrikePointNum = in.readInt();
			Collection<ShrikePoint> coll = new LinkedHashSet<ShrikePoint>();
			for(int j = 0; j < shrikePointNum; j++) {
				ShrikePoint p = (ShrikePoint)in.readObject();
				coll.add(p);
			}
			schema.locations.put(entity, coll);
		}
		return schema;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof InstrumentSchema)) {
			return false;
		}
		InstrumentSchema s = (InstrumentSchema)o;
		return this.locations.equals(s.locations);
	}
	
	@Override
	public int hashCode() {
		return this.locations.hashCode();
	}
	
	@Override
	public String toString() {
		return this.locations.toString();
	}
	
	private void buildInternals() {
		internals = new LinkedHashMap<String, Map<String, Set<Integer>>>();
		//do some classifications
		for(ConfEntity conf : this.locations.keySet()) {
			String configName = conf.getFullConfName();
			Collection<ShrikePoint> pts = this.locations.get(conf);
			for(ShrikePoint pt : pts) {
				String methodSig = pt.getMethodSig();
				Integer instructionIndex = pt.getInstructionIndex();
				//insert into the internal representation
				if(!internals.containsKey(methodSig)) {
					internals.put(methodSig, new LinkedHashMap<String, Set<Integer>>());
				}
				Map<String, Set<Integer>> confIndices = internals.get(methodSig);
				if(!confIndices.containsKey(configName)) {
					confIndices.put(configName, new LinkedHashSet<Integer>());
				}
				confIndices.get(configName).add(instructionIndex);
			}
		}
	}
}