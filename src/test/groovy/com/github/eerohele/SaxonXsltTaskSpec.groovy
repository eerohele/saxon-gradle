package com.github.eerohele

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder

import org.codehaus.groovy.runtime.typehandling.GroovyCastException

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class SaxonXsltTaskSpec extends Specification {
    final String XSLT = 'xslt'

    Project project

    String examplesDir

    void setup() {
        examplesDir = System.getProperty('examples.dir')

        project = ProjectBuilder.builder().withName(XSLT).build()
        project.configurations.create(XSLT)
    }

    Boolean assertFilesEqual(File file1, File file2) {
        Arrays.equals(
            Files.readAllBytes(Paths.get(file1.toURI())),
            Files.readAllBytes(Paths.get(file2.toURI()))
        )
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
    def 'Input file and stylesheet included in up-to-date check'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
            }

        then:
            task.getInputFiles().contains(new File("$examplesDir/simple/xml/input-1.xml"))
            task.getInputFiles().contains(new File("$examplesDir/simple/xsl/html5.xsl"))
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Uses input file basename as output file basename and stylesheet output method as extension'() {
        when:
            Task task = project.tasks.create(name: XSLT, type: SaxonXsltTask) {
                input "$examplesDir/simple/xml/input-1.xml"
                stylesheet "$examplesDir/simple/xsl/html5.xsl"
            }

        then:
            File input = new File("$examplesDir/simple/xml/input-1.xml")
            File stylesheet = new File("$examplesDir/simple/xsl/html5.xsl")
            task.getOutputFile(input, stylesheet).getName() == 'input-1.html'
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Constructing Saxon command-line arguments'() {
        expect:
            SaxonXsltTask.makeSaxonArgument('foo', 'bar') == '-foo:bar'
            SaxonXsltTask.makeSaxonArgument('dtd', true) == '-dtd:on'
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
            assertFilesEqual(outputFile, expectedFile)
    }
}
