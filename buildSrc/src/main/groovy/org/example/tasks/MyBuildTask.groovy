package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class MyBuildTask extends DefaultTask {

    @InputFile
    abstract RegularFileProperty getWarFile()

    @InputDirectory
    abstract DirectoryProperty getClassesDir()

    @Input
    abstract Property<String> getAppVersion()

    @Input
    abstract Property<String> getWildflyHome()

    @OutputFile
    abstract RegularFileProperty getJarFile()

    @TaskAction
    void build() {
        println "building JAR..."

        def jbossModules = new File(wildflyHome.get(), 'jboss-modules.jar')
        if (!jbossModules.exists()) {
            throw new GradleException("jboss-modules.jar не найден: ${jbossModules}")
        }

        def jarStageDir = project.layout.buildDirectory.dir('tmp/jar_stage').get().asFile
        if (jarStageDir.exists()) jarStageDir.deleteDir()
        jarStageDir.mkdirs()

        // классы в корень jar
        project.copy {
            from classesDir; into jarStageDir
        }

        // war как встроенный ресурс
        project.copy {
            from warFile
            into jarStageDir
            rename { 'embedded.war' }
        }

        def manifestDir = project.layout.buildDirectory.dir('tmp/manifest').get().asFile
        manifestDir.mkdirs()
        def manifestFile = new File(manifestDir, 'MANIFEST.MF')

        manifestFile.text = """\
Manifest-Version: 1.0
Implementation-Version: ${appVersion.get()}
Main-Class: org.example.Launcher
Class-Path: file://${jbossModules.absolutePath}
"""
        def destJar = jarFile.get().asFile
        destJar.parentFile.mkdirs()

        project.exec {
            commandLine "jar", "cfm",
                    destJar.absolutePath,
                    manifestFile.absolutePath,
                    "-C", jarStageDir.absolutePath, "."
        }

        println "JAR successfully built: ${destJar.name}"
    }
}