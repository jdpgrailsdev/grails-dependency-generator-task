package org.grails.gradle.task.dependency

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.grails.dependency.pom.PomBuilder
import org.slf4j.LoggerFactory

/**
 * Gradle task for generating a Grails Dependency POM file.  The dependency POM file
 * contains the dependencies required by a Grails application at runtime.
 *
 * @author Jonathan Pearlin
 */
class GrailsDependencyGenerationTask extends DefaultTask {

	static final def log = LoggerFactory.getLogger(GrailsDependencyGenerationTask.name)

	def grailsVersion

	@TaskAction
	def generate() {
		if(grailsVersion) {
			def builder = new PomBuilder(grailsVersion)
			builder.buildPom()
		} else {
			log.error("Failed to generate dependencies POM file.  Grails version must be set!")
		}
	}
}