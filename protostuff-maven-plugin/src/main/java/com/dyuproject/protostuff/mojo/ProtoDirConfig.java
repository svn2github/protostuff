package com.dyuproject.protostuff.mojo;

import java.io.Serializable;
import java.util.List;

public class ProtoDirConfig implements Serializable {

	private static final long serialVersionUID = 5547658091474270213L;
	
	/**
	 * searchablePaths
	 */
	private List<String> pathToProtoDirectories;
	
	/**
	 * Output format of the protostuff compiler
	 */
	private String output = "java_bean";
	
	/**
	 * Search for .proto files in subdirs (default=false)
	 */
	private boolean recursive;
	
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
	
	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public ProtoDirConfig() {}
}
