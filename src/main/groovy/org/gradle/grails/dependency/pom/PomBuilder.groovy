package org.gradle.grails.dependency.pom

import groovy.xml.MarkupBuilder

import org.gradle.grails.dependency.util.DependencyUtils
import org.slf4j.LoggerFactory

/**
 * Builds the Grails dependency POM file for the requested Grails version.
 * 
 * @author Jonathan Pearlin
 */
class PomBuilder {

	static final def log = LoggerFactory.getLogger(PomBuilder.name)

	def grailsDependencies
	def grailsVersion
	def pomFile

	PomBuilder(grailsVersion) {
		this.grailsVersion = grailsVersion
		this.grailsDependencies = DependencyUtils.getGrailsDependencies(grailsVersion)
		this.pomFile = new File('pom.xml')
	}

	def buildPom() {
		def writer = new FileWriter(pomFile)
		try {
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
					grailsDependencies?.each { descriptor ->
						if(DependencyUtils.isValidScope(descriptor.scope)) {
							def dep = descriptor.getDependencyRevisionId()
							dependency {
								groupId { mkp.yield dep.getOrganisation() }
								artifactId { mkp.yield dep.getName() }
								version { mkp.yield dep.getRevision() }
								if(descriptor.getAllExcludeRules()) {
									exclusions {
										descriptor.getAllExcludeRules().each { ex ->
											exclusion {
												groupId { mkp.yield DependencyUtils.getMavenizedGroupId(ex.getId().getModuleId()) }
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
				distributionManagement {
					repository {
						id { mkp.yield 'artifactory' }
						name { mkp.yield 'artifactory repo' }
						url { mkp.yield 'http://repo.grails.org/grails/libs-releases-local' }
					}
					snapshotRepository {
						uniqueVersion { mkp.yield 'true' }
						id { mkp.yield 'artifactory' }
						name { mkp.yield 'artifactory repo' }
						url { mkp.yield 'http://repo.grails.org/grails/libs-snapshots-local' }
					}
				}
			}

			log.info("Successfully generated ${pomFile.getPath()}.")
		} finally {
			writer?.flush()
			writer?.close()
		}
	}
}
