package com.dyuproject.protostuff.mojo.stubs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.ReaderFactory;

public class ProtostuffMavenPluginStub extends MavenProjectStub {

	private File baseDir;
	private Model model;
	private String pomLocation;

	public ProtostuffMavenPluginStub(String pomLocation, String baseDir) {
		this.pomLocation = pomLocation;
		this.baseDir = new File(baseDir);
		model = createPomModel();
		setProjectInfos();
		setDirectoryPaths();
		setSrcPaths();
	}

	private Model createPomModel() {
		Model model;
		try {
			MavenXpp3Reader pomReader = new MavenXpp3Reader();
			model = pomReader.read(ReaderFactory.newXmlReader(new File(pomLocation)));
			setModel(model);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return model;
	}

	private void setProjectInfos() {
		setGroupId(model.getGroupId());
		setArtifactId(model.getArtifactId());
		setVersion(model.getVersion());
		setName(model.getName());
		setUrl(model.getUrl());
		setPackaging(model.getPackaging());
	}

	private void setDirectoryPaths() {
		Build build = new Build();
		build.setFinalName(model.getArtifactId());
		build.setDirectory(getBasedir() + "/target");
		build.setSourceDirectory(getBasedir() + "/src/main/java");
		build.setOutputDirectory(getBasedir() + "/target/classes");
		build.setTestSourceDirectory(getBasedir() + "/src/test/java");
		build.setTestOutputDirectory(getBasedir() + "/target/test-classes");
		model.setBuild(build);
	}

	private void setSrcPaths() {
		List<String> compileSourceRoots = new ArrayList<String>();
		compileSourceRoots.add(getBasedir() + "/src/main/java");
		setCompileSourceRoots(compileSourceRoots);

		List<String> testCompileSourceRoots = new ArrayList<String>();
		testCompileSourceRoots.add(getBasedir() + "/src/test/java");
		setTestCompileSourceRoots(testCompileSourceRoots);
	}

	@Override
	public File getBasedir() {
		return baseDir;
	}

	@Override
	public Build getBuild() {
		return model.getBuild();
	}

	public class SettingsStub extends Settings {

		private static final long serialVersionUID = 8210937661580151495L;

		/** {@inheritDoc} */
		public List<?> getProxies() {
			return Collections.EMPTY_LIST;
		}
	}
}
