package com.github.eerohele

import org.gradle.api.Project
import org.gradle.api.Plugin

class SaxonPlugin implements Plugin<Project> {
    final String XSLT = 'xslt'

    @Override
    void apply(Project project) {
        project.task(XSLT, type: SaxonXsltTask)
    }
}
