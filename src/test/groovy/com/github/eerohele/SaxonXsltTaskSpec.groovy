package com.github.eerohele

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import spock.lang.Specification

class SaxonXsltTaskSpec extends Specification {
    final String XSLT = 'xslt'

    Project project

    String examplesDir

    void setup() {
        examplesDir = System.getProperty('examples.dir')

        project = ProjectBuilder.builder().withName(XSLT).build()
        project.configurations.create(XSLT)
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving String as input'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
            }

        then:
            task.options.input == "$examplesDir/simple/xml/input-1.xml"
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving File as input'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input new File("$examplesDir/simple/xml/input-1.xml")
            }

        then:
            task.options.input.getClass() == File
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving multiple files as input'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input project.files("$examplesDir/simple/xml/input-1.xml", "$examplesDir/simple/xml/input-2.xml")
            }

        then:
            task.options.input.contains(new File("$examplesDir/simple/xml/input-1.xml"))
            task.options.input.contains(new File("$examplesDir/simple/xml/input-2.xml"))
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving String as output'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
                output "$examplesDir/simple/build/input-1.html"
            }

        then:
            task.options.output == "$examplesDir/simple/build/input-1.html"

        and:
            notThrown GroovyCastException
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving File as output'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
                output new File("$examplesDir/simple/build/input-1.html")
            }

        then:
            task.options.output == new File("$examplesDir/simple/build/input-1.html")

        and:
            notThrown GroovyCastException
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Stylesheet path is resolved'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                input "$examplesDir/simple/xml/input-1.xml"
            }

        then:
            task.options.stylesheet.getCanonicalPath() == new File("$examplesDir/simple/xsl/html5.xsl").getCanonicalPath()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Saxon config file path is resolved'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                input "$examplesDir/simple/xml/input-1.xml"
                config "$examplesDir/simple/config/configuration.xml"
            }

        then:
            task.options.config.getCanonicalPath() == new File("$examplesDir/simple/config/configuration.xml").getCanonicalPath()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Input file and stylesheets included in up-to-date check'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"
            }

        then:
            def inputFiles = task.getInputFiles()
            inputFiles.contains(new File("$examplesDir/simple/xml/input-1.xml"))
            inputFiles.contains(new File("$examplesDir/simple/xsl/html5.xsl"))
            inputFiles.contains(new File("$examplesDir/simple/xsl/common.xsl"))
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Setting all parameters works'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                catalog 'catalog.xml'
                collectionResolver 'foo.bar.SaxonCollectionResolver'
                config 'saxon-config.xml'
                dtd true
                expand true
                explain false
                initializer 'foo.bar.SaxonInitializer'
                initialMode 'foo'
                initialTemplate 'bar'
                input "$examplesDir/simple/xml/input-1.xml"
                lineNumbers 'yes'
                messageReceiver 'foo.bar.SaxonMessageReceiver'
                output "${project.buildDir}/output.xml"
                sourceSaxParser 'foo.bar.SaxonSourceSaxParser'
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                stylesheetSaxParser 'foo.bar.SaxonStylesheetSaxParser'
                suppressJavaCalls false
                uriResolver 'foo.bar.SaxonUriResolver'
                useAssociatedStylesheet 'no'
                multipleSchemaImports true
                traceExternalFunctions true
            }

        then:
                task.options.initialTemplate == 'bar'
                task.advancedOptions.multipleSchemaImports == true
                task.advancedOptions['trace-external-functions'] == true
                noExceptionThrown()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Uses input file basename as output file basename and stylesheet output method as extension'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input project.file("$examplesDir/simple/xml/input-1.xml")
                stylesheet project.file("$examplesDir/simple/xsl/html5.xsl")
            }

        then:
            File input = new File("$examplesDir/simple/xml/input-1.xml")
            task.getOutputFile(input).getName() == 'input-1.html'
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Constructing Saxon command-line arguments'() {
        expect:
            SaxonXsltTask.makeSingleHyphenArgument('foo', 'bar') == '-foo:bar'
            SaxonXsltTask.makeSingleHyphenArgument('dtd', true) == '-dtd:on'
            SaxonXsltTask.makeDoubleHyphenArgument('multipleSchemaImports', true) == '--multipleSchemaImports:on'
            SaxonXsltTask.makeDoubleHyphenArgument('xsd-version', 1.1) == '--xsd-version:1.1'
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Setting XSLT parameters'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"

                parameters(
                    foo: 'bar',
                    baz: 'quux'
                )
            }

        then:
            task.getStylesheetParameters() == ['foo=bar', 'baz=quux']
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Running XSLT transformation'() {
        setup:
            project.apply plugin: SaxonPlugin
            project.evaluate()

        when:
            project.xslt {
                input "$examplesDir/simple/xml/input-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"

                parameters(
                    title: 'Purchase Order',
                    padding: '0.625rem'
                )
            }

            project.xslt.run()

        then:
            File outputFile = new File(project.buildDir, 'input-1.html')
            outputFile.exists()

        and:
            File expectedFile = new File("$examplesDir/simple/exp/input-1.html")
            def f1 = outputFile.getText('UTF-8').trim().replaceAll('(\\s)+', '$1')
            def f2 = expectedFile.getText('UTF-8').trim().replaceAll('(\\s)+', '$1')
            f1 == f2
    }
}
