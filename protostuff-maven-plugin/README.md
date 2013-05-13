# Protostuff Maven Plugin

Maven plugin to execute the protostuff compiler to create java classes from *.proto files. 
 
## Getting started

There are two goals to invoke the plugin. 

### compile goal 

The <code>compile</code> goal allows you to invoke the plugin with detailed configuration about your proto files.

- it is possible to configure the input using the following configuration tags:

```xml
        <configuration>
          <protoModules>
            <protoModule>
              <source>src/main/resources/foo.proto</source>
              <outputDir>src/main/java</outputDir>
              <output>java_bean</output>
              <encoding>UTF-8</encoding>
              <options>
                <property>
                  <name>generate_field_map</name>
                </property>
              </options>
            </protoModule>
          </protoModules>
        </configuration>   
```

- or using a property file for the compiler, adding the filepath to the plugin like this:

```xml
<configuration>
	<modulesFile>./src/main/resources/config.properties</modulesFile>
</configuration>
```

More information to the compile goal are available [here](https://code.google.com/p/protostuff/wiki/CompilerViaMaven) on the project site.

### compile-directory goal
			
For being able to compile all "proto" files of certain directories, you can use the <code>compile-directory</code> goal.
This is how it would look like:   

```xml
				<executions>
					<execution>
						<id>generate-protobuf-sources</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>compile-directory</goal>
						</goals>
						<configuration>
							<outputBaseDir>${project.build.generatedSourceOutput}/protostuff</outputBaseDir>
							<protoDirectory>
								<pathToProtoDirectories>
									<pathToProtoDirectory>${project.build.sourceDirectory}/../proto</pathToProtoDirectory>
								</pathToProtoDirectories>
								<output>java_bean</output>
							</protoDirectory>
						</configuration>
					</execution>
				</executions>
```

The outputBaseDir tag points to the root directory, in which the java classes will be created. The default value is <code>${project.build.Directory}/generated-sources/protostuff</code>.
(For inserting the classes afterwards in the build process, you need to invoke the "add-source" goal of the build helper plugin with the apropriate path.)

The pathToProtoDirectories tag can be filled with subtags containing a directory path. All files with the extention .proto in this folder will be compiled to
the outputBaseDir. 

The output tag indicates possible types of the output, the default value is set to <code>java_bean</code>. Look at the [plugin site](https://code.google.com/p/protostuff/wiki/CompilerViaMaven) to
know, which one to use best!  

## Colophon		

### Contributers
This maven plugin was created as a part of [protostuff](https://code.google.com/p/protostuff/).

The compile-directory goal was added by Dan Häberlein, [TIQ-Solutions](www.tiq-solutions.com), Germany.

### License 
   
All changes made are licensed under [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0). 
