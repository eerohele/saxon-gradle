package com.github.eerohele

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.nio.file.Files
import java.nio.file.Paths

import org.jsoup.Jsoup

import org.gradle.api.Project
import org.gradle.api.Task

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import spock.lang.Specification

class SaxonXsltTaskSpec extends Specification {
    final String XSLT = 'xslt'

    Project project

    String examplesDir

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    String defaultOutputDir

    File buildFile

    @SuppressWarnings(['DuplicateStringLiteral'])
    void setup() {
        examplesDir = System.getProperty('examples.dir').replace(File.separator, '/')
        project = ProjectBuilder.builder().withName(XSLT).build()
        project.configurations.create(XSLT)
        buildFile = testProjectDir.newFile("build.gradle")
        defaultOutputDir = testProjectDir.root.path.replace(File.separator, '/')
    }

    String fileAsString(File file) {
        new String(Files.readAllBytes(Paths.get(file.toURI())))
    }

    String htmlString(File file) {
        Jsoup.parse(file, "UTF-8").outerHtml()
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
        given:
        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input "$examplesDir/simple/xml/input-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"

                parameters(
                    title: 'Purchase Order',
                    padding: '0.625rem'
                )
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        File outputFile = new File(testProjectDir.root, 'build/input-1.html')
        outputFile.exists()

        and:
        File expectedFile = new File("$examplesDir/simple/exp/input-1.html")
        htmlString(outputFile) == htmlString(expectedFile)
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: input file, output file'() {
        given:

        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input "$examplesDir/simple/xml/input-1.xml"
                output "$defaultOutputDir/output-1.html"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"

                parameters(
                    title: 'Purchase Order',
                    padding: '0.625rem'
                )
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        File outputFile = new File("$defaultOutputDir/output-1.html")
        outputFile.exists()

        and:
        File expectedFile = new File("$examplesDir/simple/exp/input-1.html")
        htmlString(outputFile) == htmlString(expectedFile)
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: no input, output file'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                stylesheet "$examplesDir/no-input/xsl/no-input.xsl"
                output "$defaultOutputDir/output.xml"
                initialTemplate 'initial-template'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        File outputFile = new File(testProjectDir.root, 'output.xml')
        outputFile.exists()

        and:
        fileAsString(outputFile) == "<a/>"
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: multiple input files, non-default output directory'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input project.fileTree(dir: "$examplesDir/simple/xml", include: '*.xml')
                output "$defaultOutputDir/non-default"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"
    
                parameters(
                        title: 'Purchase Order',
                        padding: '0.625rem'
                )
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        File outputFile1 = new File("$defaultOutputDir/non-default/input-1.html")
        File outputFile2 = new File("$defaultOutputDir/non-default/input-2.html")
        outputFile1.exists()
        outputFile2.exists()

        and:
        File expectedFile1 = new File("$examplesDir/simple/exp/non-default/input-1.html")
        File expectedFile2 = new File("$examplesDir/simple/exp/non-default/input-2.html")
        htmlString(outputFile1) == htmlString(expectedFile1)
        htmlString(outputFile2) == htmlString(expectedFile2)
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: setting output file extension, single input file, no output'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input "$examplesDir/simple/xml/input-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"
    
                parameters(
                        title: 'Purchase Order',
                        padding: '0.625rem'
                )
                
                outputFileExtension 'foo'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        new File(testProjectDir.root, 'build/input-1.foo').exists()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: setting output file extension, single input file, output (conflict)'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input "$examplesDir/simple/xml/input-1.xml"
                output "$defaultOutputDir/build/output-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"
    
                parameters(
                        title: 'Purchase Order',
                        padding: '0.625rem'
                )
                
                outputFileExtension 'foo'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        new File(testProjectDir.root, 'build/output-1.xml').exists()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: setting output file extension, multiple input files, no output'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input project.fileTree(dir: "$examplesDir/simple/xml", include: '*.xml')
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"
    
                parameters(
                        title: 'Purchase Order',
                        padding: '0.625rem'
                )
                
                outputFileExtension 'foo'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        new File(testProjectDir.root, 'build/input-1.foo').exists()
        new File(testProjectDir.root, 'build/input-2.foo').exists()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Running XSLT transformation: setting output file extension, multiple input files, output'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input project.fileTree(dir: "$examplesDir/simple/xml", include: '*.xml')
                output "$defaultOutputDir/build/non-default"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
                catalog "$examplesDir/simple/catalog.xml"
    
                parameters(
                        title: 'Purchase Order',
                        padding: '0.625rem'
                )
                
                outputFileExtension 'foo'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result.task(":xslt").outcome == TaskOutcome.SUCCESS
        new File(testProjectDir.root, 'build/non-default/input-1.foo').exists()
        new File(testProjectDir.root, 'build/non-default/input-2.foo').exists()
    }
}
