package com.github.eerohele

import org.gradle.api.Project
import org.gradle.api.Plugin

abstract class SaxonPluginExtension {
    void configure(Closure cl) {
        cl.delegate = SaxonPluginConfiguration.instance
        cl()
    }
}

class SaxonPlugin implements Plugin<Project> {
    final String XSLT = 'xslt'

    @Override
    void apply(Project project) {
        project.extensions.create("saxon", SaxonPluginExtension)
        project.task(XSLT, type: SaxonXsltTask)
    }
}
