package com.github.eerohele

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification

class SaxonXsltTaskSpec extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File outputDir

    File gradle
    File xslt
    File xml1
    File xml2
    File config

    @SuppressWarnings(['DuplicateStringLiteral'])
    void setup() {
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

    String fileAsString(Path path) {
        fileAsString(path.toFile())
    }

    Path outputPath(String path) {
        Paths.get(outputDir.getPath(), path)
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
            input '${xml1.toPath()}'
            stylesheet '${xslt.toPath()}'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.xml')).equals('<b/>')
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
                input file('${xml1.toPath()}')
                output '${outputPath('xml1.xml')}'
                stylesheet '${xslt.toPath()}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.xml')).equals('<b/>')
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
                input files('${xml1.toPath()}', '${xml2.toPath()}')
                stylesheet '${xslt.toPath()}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.xml')).equals('<b/>')
        fileAsString(outputPath('xml2.xml')).equals('<c/>')
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
                input '${xml1.toPath()}'
                output '${outputPath('non-default/my-awesome-output.xml')}'
                stylesheet '${xslt.toPath()}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('non-default/my-awesome-output.xml')).equals('<b/>')
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
            input '${xml1.toPath()}'
            output file('${outputPath('non-default/my-awesome-output.xml')}')
            stylesheet '${xslt.toPath()}'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('non-default/my-awesome-output.xml')).equals('<b/>')
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
                input '${xml1.toPath()}'
                stylesheet '${xslt.toPath()}'
                config '$config'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.xml')).equals('<b/>')
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
                input '${xml1.toPath()}'
                stylesheet '${xslt.toPath()}'
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
        fileAsString(outputPath('xml1.xml')).equals('<b/>')

        when:
        def result2 = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(':xslt')
                .forwardOutput()
                .build()

        then:
        result2.task(':xslt').outcome == TaskOutcome.UP_TO_DATE
        fileAsString(outputPath('xml1.xml')).equals('<b/>')

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
        fileAsString(outputPath('xml1.xml')).equals('<c/>')
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
            input '${xml1.toPath()}'
            stylesheet '${xslt.toPath()}'
            initialMode 'foo'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.xml')).equals('<b/>')
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
                input '${xml1.toPath()}'
                stylesheet '${xslt.toPath()}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.html')).equals("<b></b>")
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
            input '${xml1.toPath()}'
            stylesheet '${xslt.toPath()}'
            parameters(
                'foo': 'bar'
            )
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.xml')).equals('<b>bar</b>')
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
                stylesheet '${xslt.toPath()}'
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
        fileAsString(outputPath('output.xml')).equals("<b/>")
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
                input '${xml1.toPath()}'
                stylesheet '${xslt.toPath()}'
                outputFileExtension 'foo'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.foo')).equals('<b/>')
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
                input '${xml1.toPath()}'
                output '${outputPath('output.xml')}'
                outputFileExtension 'foo'
                stylesheet '${xslt.toPath()}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('output.xml')).equals('<b/>')
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
            input files('${xml1.toPath()}', '${xml2.toPath()}')
            outputFileExtension 'foo'
            stylesheet '${xslt.toPath()}'
        }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('xml1.foo')).equals('<b/>')
        fileAsString(outputPath('xml2.foo')).equals('<c/>')
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
                input files('${xml1.toPath()}', '${xml2.toPath()}')
                output '${outputPath('non-default')}'
                outputFileExtension 'foo'
                stylesheet '${xslt.toPath()}'
            }
        """

        when:
        def result = execute()

        then:
        result.task(':xslt').outcome == TaskOutcome.SUCCESS
        fileAsString(outputPath('non-default/xml1.foo')).equals('<b/>')
        fileAsString(outputPath('non-default/xml2.foo')).equals('<c/>')
    }
}
