package org.gradle.grails.dependency.task

import groovy.xml.MarkupBuilder

import org.codehaus.groovy.grails.resolve.GrailsCoreDependencies
import org.codehaus.groovy.grails.resolve.IvyDependencyManager
import org.codehaus.groovy.grails.resolve.config.DependencyConfigurationConfigurer
import org.codehaus.groovy.grails.resolve.config.DependencyConfigurationContext

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import grails.util.BuildSettings

class GrailsDependencyGenerationTask extends DefaultTask {

	static final Logger slf4jLogger = LoggerFactory.getLogger(GrailsDependencyGenerationTask.name)

	def grailsVersion

	@TaskAction
	def generate() {
		if(grailsVersion) {
			def coreDependencies = new GrailsCoreDependencies(grailsVersion)
			def declarationClosure = coreDependencies.createDeclaration()
			def dependencyManager = new IvyDependencyManager("grails-dependency-generation-task", grailsVersion, new BuildSettings())
			def context = DependencyConfigurationContext.forApplication(dependencyManager)
		
	//		if (pluginName == null) {
	//		    context = DependencyConfigurationContext.forApplication(dependencyManager)
	//        } else {
	//            context = DependencyConfigurationContext.forPlugin(dependencyManager, pluginName)
	//        }

			context.setOffline(false);
			dependencyManager.setModuleDescriptor(dependencyManager.createModuleDescriptor())

	     	declarationClosure.setDelegate(new DependencyConfigurationConfigurer(context))
	        declarationClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
	        declarationClosure.call()
		
			def file = new File('pom.xml')
			def writer = new FileWriter(file)
			writer.write('<?xml version="1.0" encoding="utf-8"?>\r\n')
			def xml = new MarkupBuilder(writer)
			xml.project('xmlns':'http://maven.apache.org/POM/4.0.0', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance',
			    'xsi:schemaLocation':'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd') {
				modelVersion { mkp.yield '4.0.0'}				
				groupId { mkp.yield 'org.grails' }
				artifactId { mkp.yield 'grails-dependencies' }
				packaging { mkp.yield 'pom' }
				version { mkp.yield grailsVersion }
				name { mkp.yield 'Grails Dependencies' }
				description { mkp.yield 'POM file containing Grails dependencies.' }
				url { mkp.yield 'http://grails.org' }
				dependencies {
					dependencyManager.dependencyDescriptors.each { descriptor ->
						if(isValidScope(descriptor.scope)) {
							def dep = descriptor.getDependencyRevisionId()
							dependency {
								groupId { mkp.yield dep.getOrganisation() }
								artifactId { mkp.yield dep.getName() }
								version { mkp.yield dep.getRevision() }
								if(descriptor.getAllExcludeRules()) {
									exclusions {
										descriptor.getAllExcludeRules().each { ex ->
											exclusion {												
												groupId { mkp.yield getMavenizedGroupId(ex.getId().getModuleId()) }
												artifactId { mkp.yield ex.getId().getModuleId().getName() }												
											}
										}
									}
								}
							}
						}																				
					}			
				}
				repositories {
			        repository {
			            id { mkp.yield 'grails' }
			            name { mkp.yield 'grails' }
			            url { mkp.yield 'http://repo.grails.org/grails/core' }
					}
				}
			}

			writer.flush()
			writer.close()
			slf4jLogger.info("Successfully generated ${file.getPath()}.")
		} else {
			slf4jLogger.error("Failed to generate dependencies POM file.  Grails version must be set!")
		}
	}
	
	/**
	 * Returns the "Mavenized" group Id for a module.  This method handles the "*" used by
	 * Ivy when a group ID (or "organisation" is not provided).
	 * @param moduleId An Ivy module ID instance.
	 * @returns The group ID value to be used in a Maven POM file.
	 */
	def getMavenizedGroupId(def moduleId) {
		moduleId.getOrganisation() != '*' ? moduleId.getOrganisation() : moduleId.getName()
	}
	
	/**
	 * Check to see if the scope is one that is required at runtime by Grails (i.e. exclude all build script related dependencies).
	 * @param scope The scope of a dependency.
	 * @returns {@code true} if the dependency can be included or {@code false} if it should be skipped.
	 */
	def isValidScope(def scope) {
		'compile'.equals(scope) || 'provided'.equals(scope) || 'runtime'.equals(scope)
	}	
}