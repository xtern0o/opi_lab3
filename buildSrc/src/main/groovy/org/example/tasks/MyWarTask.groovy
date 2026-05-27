package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class MyWarTask extends DefaultTask {
    @InputDirectory
    abstract DirectoryProperty getClassesDir()

    @InputDirectory
    abstract DirectoryProperty getWebappDir()

    @InputDirectory
    abstract DirectoryProperty getResourcesDir()

    @InputFiles
    abstract ConfigurableFileCollection getRuntimeClasspath()

    @OutputFile
    abstract RegularFileProperty getWarFile()

    @TaskAction
    void war() {
        println "WARing..."

        def stageDir = project.layout.buildDirectory.dir('tmp/war_stage').get().asFile
        if (stageDir.exists()) stageDir.deleteDir()
        stageDir.mkdirs()

        project.copy {
            from webappDir; into stageDir
        }
        project.copy {
            from resourcesDir
            into new File(stageDir, 'WEB-INF/classes')
        }
        project.copy {
            from classesDir
            into new File(stageDir, 'WEB-INF/classes')
        }
        project.copy {
            from runtimeClasspath
            into new File(stageDir, 'WEB-INF/lib')
        }

        def destWar = warFile.get().asFile
        destWar.parentFile.mkdirs()

        project.exec {
            commandLine "jar", "cf", destWar.absolutePath, "-C", stageDir.absolutePath, "."
        }

        println "WAR success: ${destWar.name}"
    }
}
