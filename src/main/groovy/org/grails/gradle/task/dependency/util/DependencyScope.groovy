package org.grails.gradle.task.dependency.util

/**
 * Enumeration of the recognized dependency scopes.  This enumeration should contain all values
 * referenced in org.codehaus.groovy.grails.resolve.GrailsCoreDependencies.
 *
 * @author Jonathan Pearlin
 * @since 1.0.0
 */
enum DependencyScope {

	BUILD('compile'),
	COMPILE('compile'),
	DOCS('compile'),
	PROVIDED('provided'),
	RUNTIME('runtime'),
	TEST('test')

	/**
	 * The Maven scope that the enumerated value is mapped to.
	 */
	String mavenScope

	/**
	 * Constructs a new enumerated dependency scope value.
	 * @param mavenScope The Maven scope that this dependency scope maps to.
	 */
	DependencyScope(String mavenScope) {
		this.mavenScope = mavenScope
	}

	@Override
	public String toString() {
		name().toLowerCase()
	}

	/**
	 * Finds the enumerated value matching the provided scope name as a string.  If no match is
	 * found, the default scope (runtime) is returned.
	 * @param scopeName The name of a dependency scope.
	 * @return The matching enumerated value or the default value if no match is found.
	 */
	static def findDependencyScope(def scopeName) {
		DependencyScope.values().find { it.toString() == scopeName } ?: RUNTIME
	}
}
