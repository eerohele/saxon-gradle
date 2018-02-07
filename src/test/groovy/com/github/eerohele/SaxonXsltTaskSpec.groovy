package com.github.eerohele

import java.nio.file.Files

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

    String getNormalizedFileContent(File file) {
        file.getText('UTF-8').trim().replaceAll('(\\s)+', '$1')
    }

    Boolean areSameFiles(File file1, File file2) {
        Files.isSameFile(file1.toPath(), file2.toPath())
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving String as input'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
            }

        then:
            task.options.input.contains(project.file("$examplesDir/simple/xml/input-1.xml"))
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving File as input'() {
        when:
            File inputFile = project.file("$examplesDir/simple/xml/input-1.xml")

            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input inputFile
            }

        then:
            task.options.input.contains(inputFile)
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
            areSameFiles(task.options.output, project.file("$examplesDir/simple/build/input-1.html"))

        and:
            notThrown GroovyCastException
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Giving File as output'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
                output project.file("$examplesDir/simple/build/input-1.html")
            }

        then:
            areSameFiles(task.options.output, project.file("$examplesDir/simple/build/input-1.html"))

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

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: input file, default output dir'() {
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
            getNormalizedFileContent(outputFile) == getNormalizedFileContent(expectedFile)

        cleanup:
            project.buildDir.delete()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: input file, output file'() {
        setup:
            project.apply plugin: SaxonPlugin
            project.evaluate()

        when:
            project.xslt {
                input "$examplesDir/simple/xml/input-1.xml"
                output "$examplesDir/simple/build/output-1.html"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"

                parameters(
                        title: 'Purchase Order',
                        padding: '0.625rem'
                )
            }

            project.xslt.run()

        then:
            File outputFile = new File("$examplesDir/simple/build/output-1.html")
            outputFile.exists()

        and:
            File expectedFile = new File("$examplesDir/simple/exp/input-1.html")
            getNormalizedFileContent(outputFile) == getNormalizedFileContent(expectedFile)

        cleanup:
            project.file("$examplesDir/simple/build").delete()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: no input, output file'() {
        setup:
            project.apply plugin: SaxonPlugin
            project.evaluate()

        when:
            project.xslt {
                stylesheet "$examplesDir/no-input/xsl/no-input.xsl"
                output "${project.buildDir}/output.xml"
                initialTemplate 'initial-template'
            }

            project.xslt.run()

        then:
            File outputFile = new File(project.buildDir, 'output.xml')
            outputFile.exists()

        and:
            getNormalizedFileContent(outputFile) == "<a/>"

        cleanup:
            project.buildDir.delete()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: multiple input files, non-default output directory'() {
        setup:
            project.apply plugin: SaxonPlugin
            project.evaluate()

        when:
            project.xslt {
                stylesheet "$examplesDir/no-input/xsl/no-input.xsl"
                input project.fileTree(dir: "$examplesDir/simple/xml", include: '*.xml')
                output "$examplesDir/simple/build/non-default"
            }

            project.xslt.run()

        then:
            File outputFile1 = project.file("$examplesDir/simple/build/non-default/input-1.html")
            File outputFile2 = project.file("$examplesDir/simple/build/non-default/input-2.html")
            outputFile1.exists()
            outputFile2.exists()

        and:
            File expectedFile1 = project.file("$examplesDir/simple/exp/input-1.html")
            File expectedFile2 = project.file("$examplesDir/simple/exp/input-2.html")
            getNormalizedFileContent(outputFile1) == getNormalizedFileContent(expectedFile1)
            getNormalizedFileContent(outputFile2) == getNormalizedFileContent(expectedFile2)

        cleanup:
            project.file("$examplesDir/simple/build").delete()
    }
}
