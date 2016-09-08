package com.github.eerohele

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.file.FileCollection
import org.gradle.api.InvalidUserDataException

class SaxonXsltTask extends DefaultTask {
    private static final String PERIOD = '.'

    private final List<String> defaultArguments = ['-quit:off'].asImmutable()

    Map<String, String> options = [:]
    Map<String, String> parameters

    XmlSlurper slurper = new XmlSlurper()

    // A map from plugin arguments to Saxon command-line options.
    //
    // See http://www.saxonica.com/html/documentation/using-xsl/commandline.html
    private final Map<String, String> argumentMapping = [
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
        uriSources:              'u',
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
    private static final Map<String, String> ON_OFF = [
        'true':  'on',
        'false': 'off'
    ]

    void stylesheet(Object stylesheet) {
        this.options.stylesheet = project.file(stylesheet)
    }

    void config(Object config) {
        this.options.config = project.file(config)
    }

    @SuppressWarnings('ConfusingMethodName')
    void parameters(Map<String, String> parameters) {
        this.parameters = parameters
    }

    // Read output file extension from the <xsl:output> element of the
    // stylesheet.
    private String getDefaultOutputExtension(File stylesheet) {
        String method = this.slurper
            .parse(stylesheet)
            .declareNamespace(xsl: 'http://www.w3.org/1999/XSL/Transform')
            .output
            .@method

        return method ? method : 'xml'
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
    private File getOutputFile(File file, File stylesheet) {
        Set<File> inputFiles = project.files(this.options.input).files

        if (inputFiles.size() == 1 && this.options.output) {
            project.file(this.options.output)
        } else {
            String name = file.getName()
            String basename = name.substring(0, name.lastIndexOf(PERIOD))
            String extension = getDefaultOutputExtension(stylesheet)
            String filename = [basename, extension].join(PERIOD)
            new File(project.buildDir, filename)
        }
    }

    @OutputFiles
    FileCollection getOutputFiles() {
        project.files(inputFiles.collect {
            getOutputFile(it, project.file(this.options.stylesheet))
        })
    }

    @InputFiles
    @SkipWhenEmpty
    FileCollection getInputFiles() {
        project.files(this.options.input, this.options.stylesheet)
    }

    SaxonXsltTask() {
        slurper.setFeature('http://apache.org/xml/features/disallow-doctype-decl', false)
        slurper.setFeature('http://apache.org/xml/features/nonvalidating/load-external-dtd', false)

        ExpandoMetaClass mc = new ExpandoMetaClass(SaxonXsltTask, false, true)
        mc.initialize()
        this.metaClass = mc
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
    private static String makeSaxonArgument(key, value) {
        ['-' + key, ON_OFF.get(value.toString(), value)].join(':')
    }

    // If the user calls a missing method, assume they're trying to set an
    // option. Set the option indicated by the method name to have the value
    // indicated by the first argument to the method. Other arguments are
    // ignored.
    //
    // This probably isn't very smart (or fast), but it was a quick and easy
    // way to add support for all of Saxon's command-line arguments while
    // having a nice API.
    @SuppressWarnings('UnusedPrivateMethod')
    private Boolean methodMissing(String name, arguments) {
        Object argument = arguments[0]

        Closure cachedMethod = {
            this.options[name] = argument
        }

        this.metaClass[name] = cachedMethod
        cachedMethod(argument)
    }

    // Convert the stylesheet parameters supplied by the user to KEY=VALUE
    // pairs, which is what Saxon understands.
    //
    // TODO: Investigate how to improve support for XPath data types.
    private List<String> getStylesheetParameters() {
        this.parameters.collect { name, value ->
            [name, value].join('=')
        }.asImmutable()
    }

    // Set options common to all transformations: everything except input
    // and output.
    private List<String> getCommonArguments() {
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

    private List<String> getFileSpecificArguments(File file, File stylesheet) {
        [
            makeSaxonArgument(this.argumentMapping.input, file.getPath()),

            makeSaxonArgument(
                this.argumentMapping.output,
                getOutputFile(file, stylesheet).getPath()
            )
        ].asImmutable()
    }

    @TaskAction
    void run() {
        List<String> parameters = getStylesheetParameters()
        List<String> commonArguments = getCommonArguments()

        project.files(this.options.input).each {
            List<String> fileSpecificArguments = getFileSpecificArguments(
                it, project.file(this.options.stylesheet)
            )

            List<String> arguments = (
                fileSpecificArguments + commonArguments + parameters
            )

            SaxonTransform.main(arguments as String[])
        }
    }
}
