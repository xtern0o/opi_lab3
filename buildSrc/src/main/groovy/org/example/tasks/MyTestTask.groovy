package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.*

abstract class MyTestTask extends DefaultTask {

    @InputDirectory
    abstract DirectoryProperty getMainClassesDir()

    @InputDirectory
    abstract DirectoryProperty getTestClassesDir()

    @TaskAction
    void test() {
        println "запускаем тесты..."

        def classpath = project.sourceSets.test.runtimeClasspath.asPath +
                File.pathSeparator +
                mainClassesDir.get().asFile.absolutePath +
                File.pathSeparator +
                testClassesDir.get().asFile.absolutePath

        project.exec {
            workingDir = project.projectDir
            commandLine = [
                    'java', '-cp', classpath,
                    'org.junit.platform.console.ConsoleLauncher',
                    '--scan-classpath',
                    '--details=tree'
            ]
        }

        println "тесты успешно завершились"
    }
}