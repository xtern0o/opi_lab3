package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*

abstract class MyCompileTask extends DefaultTask {

    @InputFiles
    @SkipWhenEmpty
    abstract ConfigurableFileCollection getSources()

    @OutputDirectory
    abstract DirectoryProperty getClassesDir()

    @TaskAction
    void compile() {
        println "compiling files..."

        def destDir = classesDir.get().asFile
        destDir.mkdirs()

        def sourcesPaths = sources.files*.path
        def classpath = project.sourceSets.main.compileClasspath.asPath

        project.exec {
            commandLine(["javac", "-d", destDir.absolutePath, "-cp", classpath] + sourcesPaths)
        }

        println "compiled successfully!"
    }
}