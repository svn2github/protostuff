//========================================================================
//Copyright 2007-2010 David Yu dyuproject@gmail.com
//Copyright 2013 Dan Häberlein dan.haeberlein@tiq-solutions.de
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.protostuff.mojo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.dyuproject.protostuff.compiler.CompilerMain;

/**
 * Compiles proto files to java/gwt/etc.
 * 
 * @author Dan Häberlein
 * @created May 08, 2013
 * @goal compile-directory
 * @phase generate-sources
 * @requiresDependencyResolution runtime
 */
public class ProtoCompilerDirMojo extends AbstractMojo {

	private static class ProtoFileFilter implements FileFilter {

		private static final String PROTOBUF_FILE_EXTENTION = ".proto";

		public boolean accept(File pathname) {
			return pathname.getName().endsWith(PROTOBUF_FILE_EXTENTION);
		}
	}
	
	private static FileFilter PROTOC_FILE_FILTER  = new ProtoFileFilter(); 
	
	/**
	 * The current Maven project.
	 * 
	 * @parameter default-value="${project}"
	 * @readonly
	 * @required
	 * @since 1.0.1
	 */
	private MavenProject project;

	/**
	 * When {@code true}, skip the execution.
	 * 
	 * @parameter expression="${protostuff.compiler.skip}" default-value="false"
	 * @since 1.0.1
	 */
	private boolean skip;

	/**
	 * Usually most of protostuff mojos will not get executed on parent poms
	 * (i.e. projects with packaging type 'pom'). Setting this parameter to
	 * {@code true} will force the execution of this mojo, even if it would
	 * usually get skipped in this case.
	 * 
	 * @parameter expression="${protostuff.compiler.force}"
	 *            default-value="false"
	 * @required
	 * @since 1.0.1
	 */
	private boolean forceMojoExecution;

	/**
	 * Config for using a directory which contains .proto files. They
	 * will be compiled into outputBaseDir.
	 * 
	 * @parameter
	 * @since 1.0.8
	 */
	private ProtoDirConfig protoDirectory;

	/**
	 * If not specified, the directory
	 * "${project.build.directory}/generated-sources/protostuff" will be used as
	 * its base dir.
	 * 
	 * This is only relevent when {@link #modulesFile is provided}.
	 * 
	 * @parameter
	 * @since 1.0.8
	 */
	private File outputBaseDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!skipMojo()) {
			try {
				setDefaultOutputDirectory();
				initDirectoryListWhenEmpty();
				createProtobufsUsingDirs();
			} catch (Exception e) {
				throw new MojoExecutionException(e.getMessage(), e);
			}
		}
	}

	private void setDefaultOutputDirectory() {
		if (outputBaseDir == null)
			outputBaseDir = new File(project.getBuild().getDirectory() + "/generated-sources/protostuff");
	}

	private void initDirectoryListWhenEmpty() {
		boolean pathDirIsNull = protoDirectory.getPathToProtoDirectories() == null;
		if (pathDirIsNull) {
			protoDirectory.setPathToProtoDirectories(Collections.<String>emptyList());
		}
	}

	private void createProtobufsUsingDirs() throws Exception {
		List<String> pathToProtoDirectories = protoDirectory.getPathToProtoDirectories();
		for (String currentPathToProto : pathToProtoDirectories) {
			File currentFileHandlerToProto = new File(currentPathToProto);
			boolean exists = currentFileHandlerToProto.exists();
			boolean isDir = currentFileHandlerToProto.isDirectory();
			if (exists && isDir) {
				compileContainingProtoFiles(currentFileHandlerToProto);
			} else {
				printWarnMessage(currentFileHandlerToProto, exists, isDir);
			}
		}
	}

	private void compileContainingProtoFiles(File currentMojoDir) throws Exception {
		File[] filesList = currentMojoDir.listFiles();
		for (File currentFileHandler : filesList) {
			if(currentFileHandler.isDirectory()){
				if(protoDirectory.isRecursive()){
					compileContainingProtoFiles(currentFileHandler);
				}
			} else if(currentFileHandler.isFile() && PROTOC_FILE_FILTER.accept(currentFileHandler)){
				compileProtoFile(currentFileHandler);
			}
		}
	}

	private void compileProtoFile(File currentFileHandler) throws Exception {
		com.dyuproject.protostuff.compiler.ProtoModule module = new com.dyuproject.protostuff.compiler.ProtoModule(currentFileHandler,
																												   protoDirectory.getOutput(), 
																												   "UTF-8", outputBaseDir);
		CompilerMain.compile(module);
	}

	private void printWarnMessage(File currentMojoDir, boolean exists, boolean isDir) throws IOException {
		StringBuilder warnMessageSb = new StringBuilder();
		warnMessageSb.append("given path ");
		warnMessageSb.append(currentMojoDir.getCanonicalPath().toString());
		warnMessageSb.append(" is not valid. reason:\n");
		warnMessageSb.append("exits " + exists);
		warnMessageSb.append("\nis directory " + isDir);
		getLog().warn(warnMessageSb.toString());
	}

	/**
	 * <p>
	 * Determine if the mojo execution should get skipped.
	 * </p>
	 * This is the case if:
	 * <ul>
	 * <li>{@link #skip} is <code>true</code></li>
	 * <li>if the mojo gets executed on a project with packaging type 'pom' and
	 * {@link #forceMojoExecution} is <code>false</code></li>
	 * </ul>
	 * 
	 * @return <code>true</code> if the mojo execution should be skipped.
	 * @since 1.0.1
	 */
	protected boolean skipMojo() {
		if (skip) {
			getLog().info("Skipping protostuff mojo execution");
			return true;
		}

		if (!forceMojoExecution && project != null  && "pom".equals(project.getPackaging())) {
			getLog().info("Skipping protostuff mojo execution for project with packaging type 'pom'");
			return true;
		}

		return false;
	}

}
