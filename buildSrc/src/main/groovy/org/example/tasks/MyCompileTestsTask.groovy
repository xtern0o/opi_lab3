package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.util.Configurable

abstract class MyCompileTestsTask extends DefaultTask {
    @InputFiles
    @SkipWhenEmpty
    abstract ConfigurableFileCollection getTestSources()

    @InputDirectory
    abstract DirectoryProperty getMainClassesDir()

    @OutputDirectory
    abstract DirectoryProperty getTestClassesDir()

    @TaskAction
    void comoileTests() {
        println "compiling test files..."

        def destDir = testClassesDir.get().asFile
        destDir.mkdirs()

        def srcPaths = testSources.files*.path
        if (srcPaths.isEmpty()) {
            println "тестов не найдено("
            return
        }

        def classpath = project.sourceSets.main.compileClasspath.asPath +
                File.pathSeparator +
                project.sourceSets.test.compileClasspath.asPath +
                File.pathSeparator +
                mainClassesDir.get().asFile.absolutePath

        project.exec {
            commandLine(["javac", "-d", destDir.absolutePath, "-cp", classpath] + srcPaths)
        }

        println "test files compiled successfully"
    }

}
