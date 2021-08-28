package com.github.eerohele

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.nio.file.Files
import java.nio.file.Paths

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification

class SaxonXsltTaskSpec extends Specification {
    File systemTempDir = new File(new File(System.getProperty("java.io.tmpdir")).getCanonicalPath())

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder(systemTempDir)

    File outputDir

    File gradle
    File xslt
    File xml1
    File xml2
    File config

    String unixPath(File file) {
        file.getPath().replace('\\', '/')
    }

    @SuppressWarnings(['DuplicateStringLiteral'])
    void setup() {
        // Why do we need to do this?
        testProjectDir.create()

        gradle = testProjectDir.newFile('build.gradle')
        xslt = testProjectDir.newFile('stylesheet.xsl')

        testProjectDir.newFolder('input')
        xml1 = testProjectDir.newFile('input/xml1.xml')
        xml2 = testProjectDir.newFile('input/xml2.xml')

        config = testProjectDir.newFile('saxon-config.xml')

        outputDir = testProjectDir.newFolder('build')
    }

    String fileAsString(File file) {
        new String(Files.readAllBytes(Paths.get(file.toURI())))
    }

    File outputFile(String path) {
        new File(outputDir, path)
    }

    String outputPath(String path) {
        unixPath(outputFile(path))
    }

    private BuildResult execute() {
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'String input path'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
        plugins {
            id 'com.github.eerohele.saxon-gradle'
        }

        xslt {
            input '${unixPath(xml1)}'
            stylesheet '${unixPath(xslt)}'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'File input'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input file('${unixPath(xml1)}')
                output '${outputPath('xml1.xml')}'
                stylesheet '${unixPath(xslt)}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Multiple input files'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
                
                <xsl:template match="b">
                    <c/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''
        xml2 << '''<b/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input files('${unixPath(xml1)}', '${unixPath(xml2)}')
                stylesheet '${unixPath(xslt)}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<b/>')
        fileAsString(outputFile('xml2.xml')).equals('<c/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'String output path'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input '${unixPath(xml1)}'
                output '${outputPath('non-default/my-awesome-output.xml')}'
                stylesheet '${unixPath(xslt)}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('non-default/my-awesome-output.xml')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'File output'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
        plugins {
            id 'com.github.eerohele.saxon-gradle'
        }

        xslt {
            input '${unixPath(xml1)}'
            output file('${outputPath('non-default/my-awesome-output.xml')}')
            stylesheet '${unixPath(xslt)}'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('non-default/my-awesome-output.xml')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Saxon configuration file'() {
        given:
        config << '''
            <configuration xmlns="http://saxon.sf.net/ns/configuration">
              <xslt initialMode="foo"></xslt>
            </configuration>
        '''

        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a" mode="foo">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input '${unixPath(xml1)}'
                stylesheet '${unixPath(xslt)}'
                config '${unixPath(config)}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'Up-to-date check'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
                
                <xsl:template match="b">
                    <c/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input '${unixPath(xml1)}'
                stylesheet '${unixPath(xslt)}'
            }
        """

        when:
        def result1 = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result1.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<b/>')

        when:
        def result2 = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result2.task(':xslt').outcome == TaskOutcome.UP_TO_DATE
        fileAsString(outputFile('xml1.xml')).equals('<b/>')

        when:
        xml1.write '''<b/>'''

        and:
        def result3 = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result3.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<c/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Advanced options'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a" mode="foo">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
        plugins {
            id 'com.github.eerohele.saxon-gradle'
        }

        xslt {
            input '${unixPath(xml1)}'
            stylesheet '${unixPath(xslt)}'
            initialMode 'foo'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Deduce output file extension from stylesheet output method'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes" method="html"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input '${unixPath(xml1)}'
                stylesheet '${unixPath(xslt)}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.html')).equals("<!DOCTYPE HTML><b></b>")
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
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
                <xsl:param name="foo"/>
        
                <xsl:template match="a">
                    <b>
                        <xsl:value-of select="$foo"/>
                    </b>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
        plugins {
            id 'com.github.eerohele.saxon-gradle'
        }

        xslt {
            input '${unixPath(xml1)}'
            stylesheet '${unixPath(xslt)}'
            parameters(
                'foo': 'bar'
            )
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.xml')).equals('<b>bar</b>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral', 'DuplicateMapLiteral'])
    def 'No input file'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template name="initial-template">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                stylesheet '${unixPath(xslt)}'
                output '${outputPath('output.xml')}'
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
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('output.xml')).equals("<b/>")
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Output file extension'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input '${unixPath(xml1)}'
                stylesheet '${unixPath(xslt)}'
                outputFileExtension 'foo'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.foo')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Both output and output file extension'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input '${unixPath(xml1)}'
                output '${outputPath('output.xml')}'
                outputFileExtension 'foo'
                stylesheet '${unixPath(xslt)}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('output.xml')).equals('<b/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Output file extension (multiple files)'() {
        given:
        xslt << '''
        <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
            <xsl:output omit-xml-declaration="yes"/>
    
            <xsl:template match="a">
                <b/>
            </xsl:template>
            
            <xsl:template match="b">
                <c/>
            </xsl:template>
        </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''
        xml2 << '''<b/>'''

        gradle << """
        plugins {
            id 'com.github.eerohele.saxon-gradle'
        }

        xslt {
            input files('${unixPath(xml1)}', '${unixPath(xml2)}')
            outputFileExtension 'foo'
            stylesheet '${unixPath(xslt)}'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('xml1.foo')).equals('<b/>')
        fileAsString(outputFile('xml2.foo')).equals('<c/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Both output and output file extension (multiple files)'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>
        
                <xsl:template match="a">
                    <b/>
                </xsl:template>
                
                <xsl:template match="b">
                    <c/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''
        xml2 << '''<b/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }
    
            xslt {
                input files('${unixPath(xml1)}', '${unixPath(xml2)}')
                output '${outputPath('non-default')}'
                outputFileExtension 'foo'
                stylesheet '${unixPath(xslt)}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('non-default/xml1.foo')).equals('<b/>')
        fileAsString(outputFile('non-default/xml2.foo')).equals('<c/>')
    }

    @SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateListLiteral'])
    def 'Nested output directory layout: input under base directory'() {
        given:
        xslt << '''
            <xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output omit-xml-declaration="yes"/>

                <xsl:template match="a">
                    <b/>
                </xsl:template>

                <xsl:template match="b">
                    <c/>
                </xsl:template>
            </xsl:stylesheet>
        '''

        xml1 << '''<a/>'''
        xml2 << '''<b/>'''

        gradle << """
            plugins {
                id 'com.github.eerohele.saxon-gradle'
            }

            xslt {
                input files('${unixPath(xml1)}', '${unixPath(xml2)}')
                stylesheet '${unixPath(xslt)}'
                outputDirectoryLayout 'nested'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputFile('input/xml1.xml')).equals('<b/>')
        fileAsString(outputFile('input/xml2.xml')).equals('<c/>')
    }
}
