package org.example.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class MyCleanTask extends DefaultTask {

    @TaskAction
    void clean() {
        println "cleaning..."
        project.delete(project.layout.buildDirectory.get().asFile)
        println "cleaned successfully"
    }
}
