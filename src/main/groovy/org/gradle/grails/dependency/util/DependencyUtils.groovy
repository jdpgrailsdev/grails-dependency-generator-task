package org.gradle.grails.dependency.util

import org.codehaus.groovy.grails.resolve.EnhancedDefaultDependencyDescriptor
import org.codehaus.groovy.grails.resolve.GrailsCoreDependencies
import org.codehaus.groovy.grails.resolve.IvyDependencyManager
import org.codehaus.groovy.grails.resolve.config.DependencyConfigurationConfigurer
import org.codehaus.groovy.grails.resolve.config.DependencyConfigurationContext

import grails.util.BuildSettings

/**
 * Collection of dependency-related utility methods for retrieving and converting Ivy-based
 * dependencies.
 * 
 * @author Jonathan Pearlin
 */
class DependencyUtils {

	/**
	 * Returns the "Mavenized" group Id for a module.  This method handles the "*" used by
	 * Ivy when a group ID (or "organisation" is not provided).
	 * @param moduleId An Ivy module ID instance.
	 * @returns The group ID value to be used in a Maven POM file.
	 */
	static def getMavenizedGroupId(def moduleId) {
		moduleId.getOrganisation() != EnhancedDefaultDependencyDescriptor.WILDCARD ? moduleId.getOrganisation() : moduleId.getName()
	}
	
	/**
	 * Check to see if the scope is one that is required at runtime by Grails (i.e. exclude all build script related dependencies).
	 * @param scope The scope of a dependency.
	 * @returns {@code true} if the dependency can be included or {@code false} if it should be skipped.
	 */
	static def isValidScope(def scope) {
		'compile'.equals(scope) || 'provided'.equals(scope) || 'runtime'.equals(scope)
	}
	
	/**
	 * Generates the list of dependencies used by the requested Grails version.
	 * @param grailsVersion The version of Grails as a String.
	 * @return The list of dependencies required to run the requested version of Grails.
	 */
	static def getGrailsDependencies(def grailsVersion) {
		if(grailsVersion) {
			def coreDependencies = new GrailsCoreDependencies(grailsVersion)
			def dependencyManager = new IvyDependencyManager("grails-dependency-generation-task", grailsVersion, new BuildSettings())
			
			def context = DependencyConfigurationContext.forApplication(dependencyManager)
			context.setOffline(false)
	
			dependencyManager.setModuleDescriptor(dependencyManager.createModuleDescriptor())
	
			def declarationClosure = coreDependencies.createDeclaration()
			declarationClosure.setDelegate(new DependencyConfigurationConfigurer(context))
			declarationClosure.setResolveStrategy(Closure.DELEGATE_FIRST)
			declarationClosure.call()
			
			dependencyManager.dependencyDescriptors
		} else {
			[]
		}
	}
}
