package com.github.eerohele

import groovy.util.slurpersupport.GPathResult

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.file.FileCollection
import org.gradle.api.InvalidUserDataException

import org.apache.xml.resolver.Catalog
import org.apache.xml.resolver.CatalogManager

import net.sf.saxon.Transform

@SuppressWarnings('MethodCount')
class SaxonXsltTask extends DefaultTask {
    protected static final String PERIOD = '.'
    protected static final String XSLT_NAMESPACE = 'http://www.w3.org/1999/XSL/Transform'

    protected final List<String> defaultArguments = ['-quit:off'].asImmutable()

    Map<String, String> options = [:]
    Map<String, String> parameters

    protected XmlSlurper xmlSlurper = new XmlSlurper()
    protected GPathResult xslt
    protected Catalog xmlCatalog
    CatalogManager catalogManager = new CatalogManager()

    // A map from plugin arguments to Saxon command-line options.
    //
    // See http://www.saxonica.com/html/documentation/using-xsl/commandline.html
    protected final Map<String, String> argumentMapping = [
        catalog:                 'catalog',
        collectionResolver:      'cr',
        config:                  'config',
        dtd:                     'dtd',
        expand:                  'expand',
        explain:                 'explain',
        initializer:             'init',
        initialMode:             'im',
        initialTemplate:         'it',
        input:                   's',
        lineNumbers:             'l',
        messageReceiver:         'm',
        output:                  'o',
        sourceSaxParser:         'x',
        stylesheet:              'xsl',
        stylesheetSaxParser:     'y',
        suppressJavaCalls:       'ext',
        uriResolver:             'r',
        useAssociatedStylesheet: 'a'
    ].asImmutable()

    // Saxon command-line options take 'on' and 'off', but it's best to let the
    // users use booleans as well.
    //
    // Example:
    //
    // xslt {
    //    dtd: true
    // }
    protected static final Map<String, String> ON_OFF = [
        'true':  'on',
        'false': 'off'
    ]

    SaxonXsltTask() {
        xmlSlurper.setFeature('http://apache.org/xml/features/disallow-doctype-decl', false)
        xmlSlurper.setFeature('http://apache.org/xml/features/nonvalidating/load-external-dtd', false)
        catalogManager.setIgnoreMissingProperties(true)
    }

    void config(Object config) {
        this.options.config = project.file(config)
    }

    void collectionResolver(String resolver) {
        this.options.collectionResolver = resolver
    }

    void catalog(Object catalog) {
        this.options.catalog = project.file(catalog)

        URI catalogs = this.options.catalog.toURI()
        catalogManager.setCatalogFiles(catalogs.toString())
        xmlCatalog = new Catalog(catalogManager)
        xmlCatalog.setupReaders()
        xmlCatalog.parseCatalog(catalogs.toString())
    }

    void dtd(Object dtd) {
        this.options.dtd = dtd
    }

    void expand(Object expand) {
        this.options.expand = expand
    }

    void explain(Object explain) {
        this.options.explain = explain
    }

    void initializer(String initializer) {
        this.options.initializer = initializer
    }

    void initialMode(String initialMode) {
        this.options.initialMode = initialMode
    }

    void initialTemplate(String initialTemplate) {
        this.options.initialTemplate = initialTemplate
    }

    void input(Object input) {
        this.options.input = input
    }

    void lineNumbers(Object lineNumbers) {
        this.options.lineNumbers = lineNumbers
    }

    void messageReceiver(String receiver) {
        this.options.messageReceiver = receiver
    }

    void output(Object output) {
        this.options.output = output
    }

    void sourceSaxParser(String parser) {
        this.options.sourceSaxParser = parser
    }

    void stylesheet(Object stylesheet) {
        this.options.stylesheet = project.file(stylesheet)

        this.xslt = this.xmlSlurper
            .parse(stylesheet)
            .declareNamespace(xsl: XSLT_NAMESPACE)
    }

    void stylesheetSaxParser(String parser) {
        this.options.stylesheetSaxParser = parser
    }

    void suppressJavaCalls(Object suppress) {
        this.options.suppressJavaCalls = suppress
    }

    void uriResolver(Object resolver) {
        this.options.uriResolver = resolver
    }

    void useAssociatedStylesheet(Object use) {
        this.options.useAssociatedStylesheet = use
    }

    @SuppressWarnings('ConfusingMethodName')
    void parameters(Map<String, String> parameters) {
        this.parameters = parameters
    }

    // Read output file extension from the <xsl:output> element of the
    // stylesheet.
    protected String getDefaultOutputExtension() {
        String method = this.xslt.output.@method
        return method ? method : 'xml'
    }

    protected URI resolveUri(String path) {
        URI uri = new URI(path)

        if (this.options.catalog) {
            String resolved = xmlCatalog.resolveURI(path)
            resolved ? new URI(resolved) : uri
        } else {
            uri
        }
    }

    protected Set<File> getIncludedStylesheets(File stylesheet, Set<File> stylesheets = [].asImmutable() as Set) {
        GPathResult xslt = this.xmlSlurper.parse(stylesheet).declareNamespace(xsl: XSLT_NAMESPACE)

        [stylesheet] + (xslt.include + xslt.import).inject(stylesheets) { acc, i ->
            URI href = resolveUri(i.@href[0].toString())
            URI uri = stylesheet.toURI().resolve(href)
            acc + getIncludedStylesheets(new File(uri), acc)
        }
    }

    // Get the default output file name.
    //
    // The default output file name is the basename of the input file followed
    // by the extension extracted from the <xsl:output> element of the input
    // stylesheet.
    //
    // Would love to use commons-io for this, but I don't really want to because
    // adding into the plugin classpath causes clashes with the Gradle runtime
    // classpath.
    protected File getOutputFile(File file) {
        Set<File> inputFiles = project.files(this.options.input).files

        if (inputFiles.size() == 1 && this.options.output) {
            project.file(this.options.output)
        } else {
            String name = file.getName()
            String basename = name.substring(0, name.lastIndexOf(PERIOD))
            String extension = getDefaultOutputExtension()
            String filename = [basename, extension].join(PERIOD)
            new File(project.buildDir, filename)
        }
    }

    @OutputFiles
    FileCollection getOutputFiles() {
        project.files(inputFiles.collect {
            getOutputFile(it)
        })
    }

    @InputFiles
    @SkipWhenEmpty
    FileCollection getInputFiles() {
        project.files(this.options.input, getIncludedStylesheets(this.options.stylesheet))
    }

    // Turn a key-value pair into a Saxon command line argument.
    //
    // Examples:
    //
    //   makeSaxonArgument('foo', 'bar')
    //   ===> '-foo:bar'
    //
    //   makeSaxonArgument('dtd', true)
    //   ===> '-dtd:on'
    protected static String makeSaxonArgument(key, value) {
        ['-' + key, ON_OFF.get(value.toString(), value)].join(':')
    }

    // Convert the stylesheet parameters supplied by the user to KEY=VALUE
    // pairs, which is what Saxon understands.
    //
    // TODO: Investigate how to improve support for XPath data types.
    protected List<String> getStylesheetParameters() {
        this.parameters.collect { name, value ->
            [name, value].join('=')
        }.asImmutable()
    }

    // Set options common to all transformations: everything except input
    // and output.
    protected List<String> getCommonArguments() {
        Map<String, String> commonOptions = this.options.findAll { name, value ->
            !['input', 'output'].contains(name)
        }.asImmutable()

        commonOptions.inject(this.defaultArguments) { arguments, entry ->
            String argument = this.argumentMapping[entry.key]

            if (!argument) {
                throw new InvalidUserDataException(
                    "Invalid option: ${entry.key}."
                )
            }

            arguments + makeSaxonArgument(argument, entry.value)
        }.asImmutable()
    }

    protected List<String> getFileSpecificArguments(File file) {
        [
            makeSaxonArgument(this.argumentMapping.input, file.getPath()),

            makeSaxonArgument(
                this.argumentMapping.output,
                getOutputFile(file).getPath()
            )
        ].asImmutable()
    }

    @TaskAction
    void run() {
        List<String> parameters = getStylesheetParameters()
        List<String> commonArguments = getCommonArguments()

        project.files(this.options.input).each {
            List<String> fileSpecificArguments = getFileSpecificArguments(it)

            List<String> arguments = (
                fileSpecificArguments + commonArguments + parameters
            )

            new Transform().doTransform(arguments as String[], '')
        }
    }
}
