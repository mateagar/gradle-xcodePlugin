package org.openbakery.simulators

import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.openbakery.Type
import org.openbakery.XcodePlugin
import org.openbakery.stubs.PlistHelperStub
import org.junit.Before
import org.junit.Test
import spock.lang.Specification

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

/**
 * Created by rene on 01.09.15.
 */
class SimulatorsRunAppTaskSpecification extends Specification {

	SimulatorsRunAppTask task
	Project project
	File projectDir


	def "setup"() {

		projectDir = new File(System.getProperty("java.io.tmpdir"), "gradle-xcodebuild")
		project = ProjectBuilder.builder().withProjectDir(projectDir).build()
		project.apply plugin: org.openbakery.XcodePlugin

		task = project.tasks.findByName(XcodePlugin.SIMULATORS_RUN_APP_TASK_NAME)

	}


	def create() {
		expect:
		task instanceof SimulatorsRunAppTask
		task.simulatorControl instanceof SimulatorControl
	}


	def "hasNoInfoPlist"() {
		when:
		task.run()

		then:
		thrown(IllegalArgumentException.class)

	}


	def "no simulator SDK"() {
		given:
		project.xcodebuild.infoPlist =  "Info.plist"
		project.xcodebuild.type = Type.OSX
		when:
		task.run()

		then:
		thrown(IllegalArgumentException.class)

	}

	def "bundleIdentifier is null"() {
		given:
		project.xcodebuild.infoPlist =  "Info.plist"

		when:
		task.run()

		then:
		thrown(IllegalArgumentException.class)
	}

	def "run"() {
		given:
		project.xcodebuild.infoPlist =  "Info.plist"

		SimulatorControl simulatorControl = Mock(SimulatorControl)
		task.simulatorControl = simulatorControl

		task.plistHelper = new PlistHelperStub([
						CFBundleIdentifier: "com.example.Example"
		])

		when:
		task.run()

		then:
		1 * simulatorControl.simctl("launch", "booted", "com.example.Example")
	}


}