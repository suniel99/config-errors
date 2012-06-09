package edu.washington.cs.conf.diagnosis;

import edu.washington.cs.conf.util.Utils;

/**
 * It represents a single predicate profile datapoint, such as
 *   configuration option: maxsize
 *   context: createNewSequence
 *   evaluation count:  100
 *   entering count: 20
 * */
public class PredicateProfile {
	
//	enum POINT{EVALUATING, ENTERING};
	
	public final String confId;
	public final String context;
//	public final POINT p;
	
	private int evaluating_count = 0;
	private int entering_count = 0;
	
	public PredicateProfile(String confId, String context) {
		Utils.checkTrue(confId != null && !confId.trim().isEmpty());
		Utils.checkTrue(context != null && !context.trim().isEmpty());
		this.confId = confId;
		this.context = context;
	}
	
	public PredicateProfile(String confId, String context, 
			int evaluating_count, int entering_count) {
		this(confId, context);
		Utils.checkTrue(evaluating_count > -1);
		Utils.checkTrue(entering_count > -1);
		Utils.checkTrue(evaluating_count >= entering_count);
		this.evaluating_count = evaluating_count;
		this.entering_count = entering_count;
	}
	
	public void setEvaluatingCount(int count) {
		Utils.checkTrue(count > -1);
		this.evaluating_count = count;
	}
	
	public int getEvaluatingCount() {
		return this.evaluating_count;
	}
	
	public void setEnteringCount(int count) {
		Utils.checkTrue(count > -1);
		this.entering_count = count;
	}
	
	public int getEnteringCount() {
		return this.entering_count;
	}
	
	public float getRatio() {
		Utils.checkTrue(this.evaluating_count >= this.entering_count);
		return (float)this.entering_count/(float)this.evaluating_count;
	}
	
	/**
	 * return a default value when comparing to Zero, for example,
	 * when comparing:
	 * p1: evaluating count: 80
	 *     entering count: 20
	 * to p2, which even does not evaluate the predicate
	 * */
	public float absoluteRatio() {
		float r = this.getRatio();
		return Math.max(r, 1 - r);
	}
	
	public float importanceValue() {
		float ratio = this.getRatio();
		ratio = (ratio == 0.0f) ? 1/(float)this.evaluating_count : ratio;
		float importance = 2 / ( (1/(float)ratio) + (1/(float)this.evaluating_count));
		return importance;
	}
	
	public String getUniqueKey() {
		return confId + "@" + context;
	}
	
	public static String[] parseKey(String key) {
		return key.split("@");
	}
	
	public static String getConfig(String key) {
		return parseKey(key)[0];
	}
	
	public static String getContext(String key) {
		return parseKey(key)[1];
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof PredicateProfile)) {
			return false;
		}
		PredicateProfile p = (PredicateProfile)o;
		return this.confId.equals(p.confId)
		    && this.context.equals(p.context)
		    && this.evaluating_count == p.evaluating_count
		    && this.entering_count == p.entering_count;
	}
	
	@Override
	public int hashCode() {
		return this.confId.hashCode() + 13*this.context.hashCode()
		    + 29*this.evaluating_count + 101*this.entering_count;
	}
	
	@Override
	public String toString() {
		return confId + " @ " + context + ", evaluating: " + evaluating_count
		    + ",  entering: " + entering_count;
	}
	
}