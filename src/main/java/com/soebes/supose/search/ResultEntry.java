package com.soebes.supose.search;

import java.util.HashMap;
import java.util.Map;

import com.soebes.supose.FieldNames;

public class ResultEntry {

	private Map<String, String> fields;
	private Map<String,String> properties; //Things like svn:externals, svn:ignore etc.

	public ResultEntry() {
		fields = new HashMap<String, String>();
		properties = new HashMap<String, String>();
	}
	
	public void addField(FieldNames fieldName, String value) {
		fields.put(fieldName.getValue(), value);
	}

	public String getField(FieldNames fieldName) {
		return fields.get(fieldName.getValue());
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public void addProperty(String name, String value) {
		this.properties.put(name, value);
	}
}
