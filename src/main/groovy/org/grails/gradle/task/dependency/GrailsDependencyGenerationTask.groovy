package org.grails.gradle.task.dependency

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.grails.gradle.task.dependency.pom.PomBuilder
import org.grails.gradle.task.dependency.util.DependencyScope
import org.slf4j.LoggerFactory

/**
 * Gradle task for generating a Grails Dependency POM file.  The dependency POM file
 * contains the dependencies required by a Grails application at runtime.
 *
 * @author Jonathan Pearlin
 * @since 1.0.0
 */
class GrailsDependencyGenerationTask extends DefaultTask {

	static final def log = LoggerFactory.getLogger(GrailsDependencyGenerationTask.name)

	/**
	 * The version of Grails that will be used to generate the POM file (required).
	 */
	def grailsVersion

	/**
	 * The dependency scope to be used to filter the dependencies added to the generated POM file (defaults to 'runtime').
	 */
	def scope

	/**
	 * Generates the POM file for the requested Grails version and dependency scope.
	 */
	@TaskAction
	void generate() {
		if(grailsVersion) {
			def builder = new PomBuilder(grailsVersion, DependencyScope.findDependencyScope(scope))
			builder.buildPom()
		} else {
			log.error("Failed to generate dependencies POM file.  Grails version must be set!")
		}
	}
}