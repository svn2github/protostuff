package com.dyuproject.protostuff.mojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.dyuproject.protostuff.mojo.stubs.ProtostuffMavenPluginStub;

public class TestProtoCompilerMojo extends AbstractMojoTestCase {

	private static final String DEFAULT_BASEDIR = "./target/test-harness/protostuff";
	private static final String POM_FIXTURE_DIR = "src/test/resources/project-to-test";
	private static final String GOAL = "compile-directory";

	private String FILE_SEPARATOR = System.getProperty("file.separator");

	private File fileHandlerWorkingDir;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fileHandlerWorkingDir = new File(DEFAULT_BASEDIR);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		try {
			deleteFromDirRecursively(fileHandlerWorkingDir);
		} catch (Exception e) {
			System.err.println("cant't delete files");
			e.printStackTrace();
		}

	}
	
	private void deleteFromDirRecursively(File fileHandlerWorkingDir) {
		if (fileHandlerWorkingDir != null && fileHandlerWorkingDir.exists() && fileHandlerWorkingDir.isDirectory()) {
			File[] folderContent = fileHandlerWorkingDir.listFiles();
			for (File currentFile : folderContent) {
				if (currentFile.isFile()) {
					currentFile.delete();
				} else {
					deleteFromDirRecursively(currentFile);
					currentFile.delete();
				}
			}
		}
	}
	
	/*
	 * Tests 
	 */

	// protoDirectory Tests
	
	public void testBuildingProtosFromMultipleDirectories() throws Exception {
		executeMavenPlugin(POM_FIXTURE_DIR + "/proto_directory/multiple_dir/pom.xml");
		Set<String> expectedPaths = new HashSet<String>();
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","foo","Person.java"));
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","AddressBook.java"));
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","Name.java"));
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","Person.java"));
		checkGeneratedClasses(expectedPaths);
	}
	
	public void testBuildingProtosUsingDifferentOutputDirectory() throws Exception {
		executeMavenPlugin(POM_FIXTURE_DIR + "/proto_directory/default_dir/pom.xml");
		Set<String> expectedPaths = new HashSet<String>();
		expectedPaths.add(createFileSeparatorString("target", "generated-sources", "protostuff", "com","example","foo","Person.java"));
		expectedPaths.add(createFileSeparatorString("target", "generated-sources", "protostuff", "com","example","tutorial","AddressBook.java"));
		expectedPaths.add(createFileSeparatorString("target", "generated-sources", "protostuff", "com","example","tutorial","Name.java"));
		expectedPaths.add(createFileSeparatorString("target", "generated-sources", "protostuff", "com","example","tutorial","Person.java"));
		checkGeneratedClasses(expectedPaths);
	}
	
	public void testBuildingProtosUsingDefaultOutput() throws Exception {
		executeMavenPlugin(POM_FIXTURE_DIR + "/proto_directory/default_output/pom.xml");
		Set<String> expectedPaths = new HashSet<String>();
		expectedPaths.add(createFileSeparatorString("com","example","foo","Person.java"));
		checkGeneratedClasses(expectedPaths);
	}
	
	public void testBuildingProtosWithoutInput() throws Exception {
		executeMavenPlugin(POM_FIXTURE_DIR + "/proto_directory/empty_proto_directory/pom.xml");
		List<File> files = getFilesFromDirs(fileHandlerWorkingDir);
		assertTrue(files.isEmpty());
	}
	
	public void testRecursive() throws Exception {
		executeMavenPlugin(POM_FIXTURE_DIR + "/proto_directory/recursive_option/pom_rec.xml");
		Set<String> expectedPaths = new HashSet<String>();
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","AddressBook.java"));
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","Name.java"));
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","Person.java"));
		checkGeneratedClasses(expectedPaths);
	}
	
	public void testNonRecursive() throws Exception {
		executeMavenPlugin(POM_FIXTURE_DIR + "/proto_directory/recursive_option/pom_non_rec.xml");
		Set<String> expectedPaths = new HashSet<String>();
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","AddressBook.java"));
		expectedPaths.add(createFileSeparatorString("protostuff", "com","example","tutorial","Person.java"));
		checkGeneratedClasses(expectedPaths);
	}
	
	private void executeMavenPlugin(String pomLocation) throws Exception, MojoExecutionException, MojoFailureException {
		executeMavenPlugin(pomLocation, DEFAULT_BASEDIR);
	}
	
	private void executeMavenPlugin(String pomLocation, String baseDir) throws Exception, MojoExecutionException, MojoFailureException {
		File pom = getTestFile(pomLocation);
		assertNotNull(pom);
		assertTrue(pom.exists());
		ProtoCompilerDirMojo pcMojo = (ProtoCompilerDirMojo) lookupMojo(GOAL, pom);
		setVariableValueToObject(pcMojo, "project", new ProtostuffMavenPluginStub(pomLocation, baseDir));
		assertNotNull(pcMojo);
		pcMojo.execute();
	}
	
	private String createFileSeparatorString(String... values) {
		StringBuilder sb = new StringBuilder("");
		if (values.length > 0) {
			for (int i = 0; i < values.length - 1; i++) {
				sb.append(values[i]).append(FILE_SEPARATOR);
			}
			sb.append(values[values.length - 1]);
		}
		return sb.toString();
	}

	private void checkGeneratedClasses(Set<String> expectedPaths) throws IOException{
		checkGeneratedClasses(expectedPaths, fileHandlerWorkingDir);
	}
	
	private void checkGeneratedClasses(Set<String> expectedPaths, File fileHandler) throws IOException {
		List<File> createdJavaClasses = getFilesFromDirs(fileHandler);
		int i = 0;
		for (; i < createdJavaClasses.size(); i++) {
			String pathToCreatedJavaFile = createdJavaClasses.get(i).getCanonicalPath();
			assertTrue(pathToCreatedJavaFile + "was not created during the build process", valueContainedInPrefixSet(expectedPaths, pathToCreatedJavaFile));
		}
		assertEquals(expectedPaths.size(), i);
	}
	
	private boolean valueContainedInPrefixSet(Set<String> prefixValues, String value){
		boolean found = false;
		if(prefixValues != null)
			for(String currentPrefix : prefixValues){
				if(value.endsWith(currentPrefix)){
					found = true;
					break;
				}
			}
		return found;
	}
	
	private List<File> getFilesFromDirs(File fileHandlerWorkingDir) {
		List<File> foundFiles = new ArrayList<File>();
		if (fileHandlerWorkingDir != null && fileHandlerWorkingDir.exists()) {
			if (fileHandlerWorkingDir.isDirectory()) {
				File[] folderContent = fileHandlerWorkingDir.listFiles();
				for (File currentFile : folderContent) {
					if (currentFile.isFile()) {
						foundFiles.add(currentFile);
					} else {
						foundFiles.addAll(getFilesFromDirs(currentFile));
					}
				}

			} else {
				foundFiles.add(fileHandlerWorkingDir);
			}
		}
		return foundFiles;
	}

}
