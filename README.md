Grails Dependency Generator Gradle Task
=======================================

The Grails Dependency Generator Task is a Gradle task that can produce the various Maven POM files required to build Grails applications and plugins.  The task uses the `org.codehaus.groovy.grails.resolve.GrailsCoreDependencies` class from the `grails-bootstrap` module to determine the dependencies (and their exclusions) that should be included in the POM for the requested scope.

Usage
-----

In order to use the Grails Dependency Generator Task, the project must first be built and pushed to a repository:

	./gradle clean install
	
Once the task JAR has been built and pushed to your local repository, you can use the task in any Gradle project:

	buildscript {
	    repositories {
	        mavenLocal()
	        maven {
	            url 'http://repo.grails.org/grails/core'
	        }
	        mavenCentral()
	    }
	
	    dependencies {
			classpath("org.grails:grails-dependency-generator-task:1.0.0-SNAPSHOT") {
				transitive = false
			}
		    classpath("org.grails:grails-bootstrap:2.1.1") {
				transitive = false
			}
	    }
	}

	...
	
	task generate(type: org.grails.gradle.task.dependency.GrailsDependencyGenerationTask) {
    	grailsVersion = '2.1.1'
		scope = 'runtime'
	}

Parameters
----------
The Gradle task supports the following parameters/properties:

* `grailsVersion` The version of Grails used to generate the POM file (`required`, no default)
* `scope` The dependency scope used to select dependencies for inclusion in the generated POM file (`optional`, default = `runtime`)

Generated POM File
------------------

The Gradle task generates a Maven POM file with the following GAV:

	<groupId>org.grails</groupId>
	<artifactId>grails-${dependencyScope}-dependencies</artifactId>
	<packaging>pom</packaging>
	<version>${grailsVersion}</version>
	
where the `dependencyScope` is the value `scope` parameter passed to the task and the `grailsVersion` is the `grailsVersion` scoped also passed to the task. 
	
Scopes
------

The Grails Dependency Generator Task currently supports the following scopes:

* `build` (mapped to the Maven `compile` scope)
* `compile` (mapped to the Maven `compile` scope)
* `docs` (mapped to the Maven `compile` scope)
* `provided` (mapped to the Maven `provided` scope)
* `runtime` (mapped to the Maven `runtime` scope)
* `test` (mapped to the Maven `test` scope)

These scopes map to the Ivy scopes used in the aforementioned `GrailsCoreDependencies` class.  By default, a scope value of `runtime` is used if no value is provided to the task.
