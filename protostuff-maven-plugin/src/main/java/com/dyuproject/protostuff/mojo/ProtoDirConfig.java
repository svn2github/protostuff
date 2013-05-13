package com.dyuproject.protostuff.mojo;

import java.io.Serializable;
import java.util.List;

public class ProtoDirConfig implements Serializable {

	private static final long serialVersionUID = 5547658091474270213L;
	
	private List<String> pathToProtoDirectories;
	private String output = "java_bean";
	
	public List<String> getPathToProtoDirectories() {
		return pathToProtoDirectories;
	}
	
	public void setPathToProtoDirectories(List<String> pathToProtoDirectories) {
		this.pathToProtoDirectories = pathToProtoDirectories;
	}
		
	public String getOutput() {
		return output;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public ProtoDirConfig() {}
}
