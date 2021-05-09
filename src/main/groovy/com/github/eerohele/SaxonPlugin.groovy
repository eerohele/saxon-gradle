package com.github.eerohele

import org.gradle.api.Project
import org.gradle.api.Plugin

abstract class SaxonPluginExtension {
    void configure(Closure cl) {
        this.configure(SaxonPluginConfigurations.DEFAULT, cl)
    }
    
    void configure(String name, Closure cl) {
        cl.delegate = new SaxonPluginConfiguration(name)
        cl()
    }
}
class SaxonPlugin implements Plugin<Project> {
    final String XSLT = 'xslt'
    final String SAXON = 'saxon'

    @Override
    void apply(Project project) {
        project.extensions.create(SAXON, SaxonPluginExtension)
        project.task(XSLT, type: SaxonXsltTask)
    }
}
