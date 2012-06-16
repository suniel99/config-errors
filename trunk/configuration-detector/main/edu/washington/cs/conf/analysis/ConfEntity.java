package edu.washington.cs.conf.analysis;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

/**
 * This class encapsulates a configuration option.
 * A configuration option is often represented by
 * a class field 
 * */
public class ConfEntity implements Serializable {
	
	private static final long serialVersionUID = 5179583036405221484L;
	
	private final String className;
	private final String confName;
	private final boolean isStatic;
	
	private Class<?> type = null;
	private String assignMethod = null; //null is the default
	
	public ConfEntity(String className, String confName, boolean isStatic) {
		this(className, confName, null, isStatic);
	}
	
	public ConfEntity(String className, String confName, String affMethod,
			boolean isStatic) {
		assert className != null;
		assert confName != null;
		this.className = className;
		this.confName = confName;
		this.assignMethod = affMethod;
		this.isStatic = isStatic;
		//get the class
//		Field field = Utils.lookupField(className, confName);
//		this.type = field.getDeclaringClass();
//		this.isStatic = Modifier.isStatic(field.getModifiers());
	}
	
	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public boolean isStatic() {
		return isStatic;
	}
	
	public String getAssignMethod() {
		return assignMethod;
	}

	public void setAssignMethod(String assignMethod) {
		this.assignMethod = assignMethod;
	}

	public String getClassName() {
		return className;
	}

	public String getConfName() {
		return confName;
	}
	
	public String getFullConfName() {
		return className + "." + confName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ConfEntity) {
			ConfEntity e = (ConfEntity)obj;
			return e.className.equals(this.className)
			    && e.confName.equals(this.confName)
//			    && e.type.equals(this.type)
			    && e.isStatic == this.isStatic;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return className + " : " + confName + " @ " + assignMethod
		    + ", " + type + ", static: " + isStatic;
	}
}