package com.github.eerohele

import org.xml.sax.InputSource
import javax.xml.transform.sax.SAXSource

import net.sf.saxon.s9api.DocumentBuilder
import net.sf.saxon.s9api.Processor
import net.sf.saxon.s9api.QName
import net.sf.saxon.s9api.SaxonApiException
import net.sf.saxon.s9api.XPathExecutable
import net.sf.saxon.s9api.XdmNode

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.file.FileCollection
import org.gradle.api.InvalidUserDataException

import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject
import java.nio.file.Path

class SaxonXsltTask extends DefaultTask implements SaxonPluginOptions {
    protected static final String PERIOD = '.'
    protected static final String XSLT_NAMESPACE = 'http://www.w3.org/1999/XSL/Transform'
    protected static final QName METHOD = new QName('method')
    protected static final String INPUT_OPTION = 'input'
    protected static final String OUTPUT_OPTION = 'output'
    protected static final String STYLESHEET_OPTION = 'stylesheet'

    protected final List<String> defaultArguments = ['-quit:off'].asImmutable()

    protected final Map<String, Object> options = [output: project.buildDir]
    protected final Map<String, String> advancedOptions = [:]
    protected final Map<String, Object> pluginOptions = [outputDirectoryLayout: 'flat']

    protected Map<String, String> stylesheetParams = [:]

    private final Processor processor
    private final DocumentBuilder builder
    private final XPathExecutable findOutput
    private XdmNode xslt = null

    // Saxon command-line options take 'on' and 'off', but it's best to let the
    // users use booleans as well.
    //
    // Example:
    //
    // xslt {
    //    dtd: true
    // }
    protected static final Map<String, String> ON_OFF = [
            'true' : 'on',
            'false': 'off'
    ]

    private final WorkerExecutor workerExecutor

    @Inject
    SaxonXsltTask(WorkerExecutor workerExecutor) {
        super()
        this.workerExecutor = workerExecutor

        processor = new Processor(false)
        builder = processor.newDocumentBuilder()
        builder.setDTDValidation(false)
        builder.setLineNumbering(true)

        def xpc = processor.newXPathCompiler()
        xpc.declareNamespace("xsl", XSLT_NAMESPACE)
        findOutput = xpc.compile("//xsl:output[not(@name)]")
    }

    // ============================================================

    void setOption(String name, Object value) {
        options[name] = value
    }

    void setAdvancedOption(String name, Object value) {
        advancedOptions[name] = value
    }

    void setPluginOption(String name, Object value) {
        pluginOptions[name] = value
    }

    Object getOption(String name) {
        return name in options ? options[name] :
            SaxonPluginConfiguration.instance.options[name]
    }

    Object getAdvancedOption(String name) {
        return name in advancedOptions ? advancedOptions[name] :
            SaxonPluginConfiguration.instance.advancedOptions[name]
    }

    Object getPluginOption(String name) {
        return name in pluginOptions ? pluginOptions[name] :
            SaxonPluginConfiguration.instance.pluginOptions[name]
    }

    // ============================================================

    void input(Object input) {
        setOption(INPUT_OPTION, input)
    }

    void output(Object output) {
        setOption(OUTPUT_OPTION, project.file(output))
    }

    void stylesheet(Object stylesheet) {
        this.xslt = null
        try {
            // If it's a string that looks like a URI, try to make it a URI
            if (stylesheet instanceof String && stylesheet ==~ /^\S+:\/\/.*/) {
                try {
                    stylesheet = new URI(stylesheet)
                } catch (URISyntaxException ex) {
                    // nevermind
                }
            }

            if (stylesheet instanceof URI) {
                // If it's a file: URI, turn it into a file
                if (stylesheet.getScheme() == "file") {
                    def stylefile = project.file(stylesheet.getPath())
                    setOption(STYLESHEET_OPTION, stylefile)
                    if (stylefile.exists()) {
                        this.xslt = builder.build(stylefile)
                    }
                } else {
                    // Otherwise just try to load it (for the output method)
                    setOption(STYLESHEET_OPTION, stylesheet)
                    def source = new InputSource(stylesheet.toASCIIString())
                    this.xslt = builder.build(new SAXSource(source))
                }
            } else {
                // If it's not a URI, assume it's a file, load it if it exists
                def stylefile = project.file(stylesheet)
                setOption(STYLESHEET_OPTION, stylefile)
                if (stylefile.exists()) {
                    this.xslt = builder.build(stylefile)
                }
            }
        } catch (SaxonApiException ex) {
            logger.warn("Failed to parse: ${stylesheet}")
            logger.warn("  ${ex.getMessage()}")
        }
    }

    void parameters(Map<String, String> parameters) {
        this.stylesheetParams = parameters
    }

    @Internal
    protected String getDefaultOutputExtension() {
        if (getPluginOption('outputFileExtension') != null) {
            return getPluginOption('outputFileExtension')
        }

        // Read output file extension from the <xsl:output> element of the
        // stylesheet. This is only a heuristic.
        String method = null
        if (this.xslt != null) {
            def selector = findOutput.load()
            selector.setContextItem(this.xslt)
            selector.iterator().each { snode ->
                method = snode.getAttributeValue(METHOD)
            }
        }

        return method ? method : 'xml'
    }

    String getOutputFileName(File file) {
        String name = file.getName()
        String basename = name.substring(0, name.lastIndexOf(PERIOD))
        String extension = getDefaultOutputExtension()
        [basename, extension].join(PERIOD)
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
        Path filePath = file.toPath()
        Path rootPath = project.rootDir.toPath()
        Path outputPath = project.file(getOption(OUTPUT_OPTION)).toPath()

        if (getPluginOption('outputDirectoryLayout') == 'nested'
            && filePath.startsWith(rootPath) && outputPath.startsWith(rootPath)) {
            Path dir = rootPath.relativize(filePath).parent
            outputPath.resolve(dir).resolve(getOutputFileName(file)).toFile()

            // Using outputDirectoryLayout == 'nested' in scenarios where the
            // input or output path is *not* under the project base directory is
            // currently undefined behavior. I currently don't have the time to
            // enumerate all the edge cases, of which there are numerous.
            //
            // Similarly, this approach likely won't work with symlinks, because
            // to resolve symlinks, we would need to call .toRealPath(), but
            // that presumes that the target file or directory exists, which
            // is often not necessarily the case with this plugin. Another thing
            // I don't have the time to figure out.
            //
            // Anyway, this approach likely covers a large chunk of how people
            // would conceivably use this feature anyway.
        } else if (project.files(getOption(INPUT_OPTION)).size() == 1
                   && project.buildDir.getAbsolutePath() != getOption(OUTPUT_OPTION).getAbsolutePath()) {
            getOption(OUTPUT_OPTION)
        } else {
            new File(getOption(OUTPUT_OPTION), getOutputFileName(file))
        }
    }

    @OutputFiles
    FileCollection getOutputFiles() {
        if (getOption(INPUT_OPTION) != null) {
            project.files(project.files(getOption(INPUT_OPTION)).collect { getOutputFile(it) })
        } else {
            project.files(getOption(OUTPUT_OPTION))
        }
    }

    @InputFiles
    @SkipWhenEmpty
    FileCollection getInputFiles() {
        FileCollection files = project.files()
        if (getOption(INPUT_OPTION) != null) {
            files += project.files(getOption(INPUT_OPTION))
        }
        if (getOption(STYLESHEET_OPTION) != null && getOption(STYLESHEET_OPTION) instanceof File) {
            files += project.files(getOption(STYLESHEET_OPTION))
        }
        return files
    }

    // Turn a key-value pair into a Saxon command line argument.
    //
    // Examples:
    //
    //   makeSaxonArgument('-', 'foo', 'bar')
    //   ===> '-foo:bar'
    //
    //   makeSaxonArgument('-', 'dtd', true)
    //   ===> '-dtd:on'
    protected static String makeSaxonArgument(prefix, key, value) {
        [prefix + key, ON_OFF.get(value.toString(), value)].join(':')
    }

    protected static String makeSingleHyphenArgument(key, value) {
        makeSaxonArgument('-', key, value)
    }

    protected static String makeDoubleHyphenArgument(key, value) {
        makeSaxonArgument('--', key, value)
    }

    // Convert the stylesheet parameters supplied by the user to KEY=VALUE
    // pairs, which is what Saxon understands.
    //
    // TODO: Investigate how to improve support for XPath data types.
    @Internal
    protected List<String> getStylesheetParameters() {
        this.stylesheetParams.collect { name, value ->
            [name, value].join('=')
        }.asImmutable()
    }

    // Set options common to all transformations: everything except input
    // and output.
    @Internal
    protected List<String> getCommonArguments() {
        Map<String, String> commonOptions = [:]
        SaxonPluginConfiguration.instance.options.findAll { name, value ->
            commonOptions[name] = value
        }
        this.options.findAll { name, value ->
            if (name != INPUT_OPTION && name != OUTPUT_OPTION) {
                commonOptions[name] = value
            }
        }

        SaxonPluginConfiguration.instance.advancedOptions.findAll { name, value ->
            advancedOptions[name] = value
        }
        this.advancedOptions.findAll { name, value ->
            advancedOptions[name] = value
        }

        (commonOptions.inject(this.defaultArguments) { arguments, entry ->
            String defaultArgument = Argument.MAPPING[entry.key]
            if (defaultArgument == null) {
                throw new InvalidUserDataException(
                        "Invalid option: ${entry.key}."
                )
            }

            arguments + makeSingleHyphenArgument(defaultArgument, entry.value)
        } + advancedOptions.collect {
            makeDoubleHyphenArgument(it.key, it.value)
        }).asImmutable()
    }

    protected List<String> getFileSpecificArguments(File file) {
        String inputPath = file.getPath()
        String outputPath = getOutputFile(file).getPath()

        [makeSingleHyphenArgument(Argument.MAPPING.input, inputPath),
         makeSingleHyphenArgument(Argument.MAPPING.output, outputPath)].asImmutable()
    }

    @TaskAction
    void run() {
        WorkQueue workQueue = workerExecutor.classLoaderIsolation() {
            if (getPluginOption('classpath') != null) {
                it.getClasspath().from(getPluginOption('classpath'))
            }
        };

        List<String> commonArguments = getCommonArguments() + getStylesheetParameters()

        if (getOption(INPUT_OPTION) != null) {
            project.files(getOption(INPUT_OPTION)).each {
                List<String> arguments = getFileSpecificArguments(it) + commonArguments

                workQueue.submit(XsltTransformation) {
                    it.arguments.set(arguments)
                }
            }
        } else {
            String output = getOption(OUTPUT_OPTION).getPath()
            List<String> arguments = commonArguments + [makeSingleHyphenArgument(Argument.MAPPING.output, output)]

            workQueue.submit(XsltTransformation) {
                it.arguments.set(arguments)
            }
        }
    }
}
