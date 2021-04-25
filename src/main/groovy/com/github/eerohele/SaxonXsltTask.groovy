package com.github.eerohele

import groovy.util.slurpersupport.GPathResult
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.file.FileCollection
import org.gradle.api.InvalidUserDataException

import org.apache.xml.resolver.Catalog
import org.apache.xml.resolver.CatalogManager

import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject
import java.nio.file.Path

@SuppressWarnings('MethodCount')
class SaxonXsltTask extends DefaultTask {
    protected static final String PERIOD = '.'
    protected static final String XSLT_NAMESPACE = 'http://www.w3.org/1999/XSL/Transform'

    protected final List<String> defaultArguments = ['-quit:off'].asImmutable()

    protected final Map<String, Object> options = [output: project.buildDir]

    protected final Map<String, Object> pluginOptions = [outputDirectoryLayout: 'flat']

    @Classpath
    final ConfigurableFileCollection classpath

    protected final Map<String, String> advancedOptions = [:]

    protected Map<String, String> stylesheetParams = [:]

    private final XmlSlurper xmlSlurper = new XmlSlurper()
    private GPathResult xslt
    private Catalog xmlCatalog
    private final CatalogManager catalogManager = new CatalogManager()

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
        this.classpath = project.objects.fileCollection()
        xmlSlurper.setFeature('http://apache.org/xml/features/disallow-doctype-decl', false)
        xmlSlurper.setFeature('http://apache.org/xml/features/nonvalidating/load-external-dtd', false)
        catalogManager.setIgnoreMissingProperties(true)
    }

    // START COMMON OPTIONS

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
        this.options.output = project.file(output)
    }

    void sourceSaxParser(String parser) {
        this.options.sourceSaxParser = parser
    }

    @SuppressWarnings('CatchException')
    void stylesheet(Object stylesheet) {
        this.options.stylesheet = project.file(stylesheet)
        if (this.options.stylesheet.exists()) {
            try {
                this.xslt = this.xmlSlurper
                        .parse(stylesheet)
                        .declareNamespace(xsl: XSLT_NAMESPACE)
            } catch (Exception ex) {
                logger.warn("Failed to parse: ${this.options.stylesheet}")
                logger.warn("  ${ex.getMessage()}")
            }
        }
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

    void outputFileExtension(String extension) {
        this.pluginOptions.outputFileExtension = extension
    }

    void outputDirectoryLayout(String layout) {
        this.pluginOptions.outputDirectoryLayout = layout
    }

    // START ADVANCED OPTIONS

    void allowExternalFunctions(Boolean x) {
        this.advancedOptions['allow-external-functions'] = x;
    }

    void allowMultithreading(Boolean x) {
        this.advancedOptions['allow-multithreading'] = x;
    }

    void allowOldJavaURIFormat(Boolean x) {
        this.advancedOptions['allow-old-java-uri-format'] = x;
    }

    void allowSyntaxExtensions(Boolean x) {
        this.advancedOptions.allowSyntaxExtensions = x;
    }

    void assertionsCanSeeComments(Boolean x) {
        this.advancedOptions.assertionsCanSeeComments = x;
    }

    void collationUriResolver(Object x) {
        this.advancedOptions['collation-uri-resolver'] = x;
    }

    void collationUriResolverClass(Object x) {
        this.advancedOptions['collation-uri-resolver-class'] = x;
    }

    void collectionFinder(Object x) {
        this.advancedOptions['collection-finder'] = x;
    }

    void collectionFinderClass(Object x) {
        this.advancedOptions['collection-finder-class'] = x;
    }

    void collectionUriResolver(Object x) {
        this.advancedOptions['collection-uri-resolver'] = x;
    }

    void collectionUriResolverClass(Object x) {
        this.advancedOptions['collection-uri-resolver-class'] = x;
    }

    void compileWithTracing(Boolean x) {
        this.advancedOptions.with = x;
    }

    void configuration(Object x) {
        this.advancedOptions.configuration = x;
    }

    void configurationFile(Object x) {
        this.advancedOptions['configuration-file'] = x;
    }

    void debugByteCode(Object x) {
        this.advancedOptions.debugByteCode = x;
    }

    void debugByteCodeDir(Object x) {
        this.advancedOptions.debugByteCodeDir = x;
    }

    void defaultCollation(Object x) {
        this.advancedOptions.defaultCollation = x;
    }

    void defaultCollection(Object x) {
        this.advancedOptions.defaultCollection = x;
    }

    void defaultCountry(Object x) {
        this.advancedOptions.defaultCountry = x;
    }

    void defaultLanguage(Object x) {
        this.advancedOptions.defaultLanguage = x;
    }

    void defaultRegexEngine(Object x) {
        this.advancedOptions.defaultRegexEngine = x;
    }

    void disableXslEvaluate(Boolean x) {
        this.advancedOptions.disableXslEvaluate = x;
    }

    void displayByteCode(Boolean x) {
        this.advancedOptions.displayByteCode = x;
    }

    void validation(Object x) {
        this.advancedOptions.validation = x;
    }

    void dtdValidationRecoverable(Object x) {
        this.advancedOptions['dtd-validation-recoverable'] = x;
    }

    void eagerEvaluation(Object x) {
        this.advancedOptions.eagerEvaluation = x;
    }

    void entityResolverClass(Object x) {
        this.advancedOptions.entityResolverClass = x;
    }

    void environmentVariableResolver(Object x) {
        this.advancedOptions.environmentVariableResolver = x;
    }

    void environmentVariableResolverClass(Object x) {
        this.advancedOptions.environmentVariableResolverClass = x;
    }

    void errorListenerClass(Object x) {
        this.advancedOptions.errorListenerClass = x;
    }

    void expathFileDeleteTemporaryFiles(Object x) {
        this.advancedOptions.expathFileDeleteTemporaryFiles = x;
    }

    void generateByteCode(Object x) {
        this.advancedOptions.generateByteCode = x;
    }

    void ignoreSAXSourceParser(Object x) {
        this.advancedOptions.ignoreSAXSourceParser = x;
    }

    void implicitSchemaImports(Object x) {
        this.advancedOptions.implicitSchemaImports = x;
    }

    void lazyConstructionMode(Object x) {
        this.advancedOptions.lazyConstructionMode = x;
    }

    void licenseFileLocation(Object x) {
        this.advancedOptions.licenseFileLocation = x;
    }

    void markDefaultedAttributes(Object x) {
        this.advancedOptions.markDefaultedAttributes = x;
    }

    void maxCompiledClasses(Object x) {
        this.advancedOptions.maxCompiledClasses = x;
    }

    void messageEmitterClass(Object x) {
        this.advancedOptions.messageEmitterClass = x;
    }

    void moduleURIResolver(Object x) {
        this.advancedOptions.moduleURIResolver = x;
    }

    void moduleURIResolverClass(Object x) {
        this.advancedOptions.moduleURIResolverClass = x;
    }

    void monitorHotSpotByteCode(Object x) {
        this.advancedOptions.monitorHotSpotByteCode = x;
    }

    void multipleSchemaImports(Object x) {
        this.advancedOptions.multipleSchemaImports = x;
    }

    void namePool(Object x) {
        this.advancedOptions.namePool = x;
    }

    void occurrenceLimits(Object x) {
        this.advancedOptions.occurrenceLimits = x;
    }

    void optimizationLevel(Object x) {
        this.advancedOptions.optimizationLevel = x;
    }

    void outputURIResolver(Object x) {
        this.advancedOptions.outputURIResolver = x;
    }

    void outputURIResolverClass(Object x) {
        this.advancedOptions.outputURIResolverClass = x;
    }

    void parserFeature(Object x) {
        this.advancedOptions.parserFeature = x;
    }

    void parserProperty(Object x) {
        this.advancedOptions.parserProperty = x;
    }

    void preEvaluateDocFunction(Object x) {
        this.advancedOptions.preEvaluateDocFunction = x;
    }

    void preferJaxpParser(Object x) {
        this.advancedOptions.preferJaxpParser = x;
    }

    void recognizeUriQueryParameters(Object x) {
        this.advancedOptions['recognize-uri-query-parameters'] = x;
    }

    void recoveryPolicy(Object x) {
        this.advancedOptions.recoveryPolicy = x;
    }

    void recoveryPolicyName(Object x) {
        this.advancedOptions.recoveryPolicyName = x;
    }

    void resultDocumentThreads(Object x) {
        this.advancedOptions.resultDocumentThreads = x;
    }

    void retainDtdAttributeTypes(Object x) {
        this.advancedOptions['retain-dtd-attribute-types'] = x;
    }

    void schemaURIResolver(Object x) {
        this.advancedOptions.schemaURIResolver = x;
    }

    void schemaURIResolverClass(Object x) {
        this.advancedOptions.schemaURIResolverClass = x;
    }

    void schemaValidation(Object x) {
        this.advancedOptions['schema-validation'] = x;
    }

    void schemaValidationMode(Object x) {
        this.advancedOptions['schema-validation-mode'] = x;
    }

    void serializerFactoryClass(Object x) {
        this.advancedOptions.serializerFactoryClass = x;
    }

    void sourceParserClass(Object x) {
        this.advancedOptions.sourceParserClass = x;
    }

    void sourceResolverClass(Object x) {
        this.advancedOptions.sourceResolverClass = x;
    }

    void stableCollectionUri(Object x) {
        this.advancedOptions.stableCollectionUri = x;
    }

    void stableUnparsedText(Object x) {
        this.advancedOptions.stableUnparsedText = x;
    }

    void standardErrorOutputFile(Object x) {
        this.advancedOptions.standardErrorOutputFile = x;
    }

    void streamability(Object x) {
        this.advancedOptions.streamability = x;
    }

    void strictStreamability(Object x) {
        this.advancedOptions.strictStreamability = x;
    }

    void streamingFallback(Object x) {
        this.advancedOptions.streamingFallback = x;
    }

    void stripWhitespace(Object x) {
        this.advancedOptions['strip-whitespace'] = x;
    }

    void styleParserClass(Object x) {
        this.advancedOptions.styleParserClass = x;
    }

    void suppressEvaluationExpiryWarning(Object x) {
        this.advancedOptions.suppressEvaluationExpiryWarning = x;
    }

    void suppressXPathWarnings(Object x) {
        this.advancedOptions.suppressXPathWarnings = x;
    }

    void suppressXsltNamespaceCheck(Object x) {
        this.advancedOptions.suppressXsltNamespaceCheck = x;
    }

    void thresholdForCompilingTypes(Object x) {
        this.advancedOptions.thresholdForCompilingTypes = x;
    }

    void timing(Object x) {
        this.advancedOptions.timing = x;
    }

    void traceExternalFunctions(Object x) {
        this.advancedOptions['trace-external-functions'] = x;
    }

    void traceListener(Object x) {
        this.advancedOptions.traceListener = x;
    }

    void traceListenerClass(Object x) {
        this.advancedOptions.traceListenerClass = x;
    }

    void traceListenerOutputFile(Object x) {
        this.advancedOptions.traceListenerOutputFile = x;
    }

    void traceOptimizerDecisions(Object x) {
        this.advancedOptions['trace-optimizer-decisions'] = x;
    }

    void treeModel(Object x) {
        this.advancedOptions.treeModel = x;
    }

    void treeModelName(Object x) {
        this.advancedOptions.treeModelName = x;
    }

    void unparsedTextURIResolver(Object x) {
        this.advancedOptions.unparsedTextURIResolver = x;
    }

    void unparsedTextURIResolverClass(Object x) {
        this.advancedOptions.unparsedTextURIResolverClass = x;
    }

    void uriResolverClass(Object x) {
        this.advancedOptions.uriResolverClass = x;
    }

    void usePiDisableOutputEscaping(Object x) {
        this.advancedOptions['use-pi-disable-output-escaping'] = x;
    }

    void useTypedValueCache(Object x) {
        this.advancedOptions['use-typed-value-cache'] = x;
    }

    void useXsiSchemaLocation(Object x) {
        this.advancedOptions.useXsiSchemaLocation = x;
    }

    void validationComments(Object x) {
        this.advancedOptions['validation-comments'] = x;
    }

    void validationWarnings(Object x) {
        this.advancedOptions['validation-warnings'] = x;
    }

    void versionWarning(Object x) {
        this.advancedOptions['version-warning'] = x;
    }

    void xincludeAware(Object x) {
        this.advancedOptions['xinclude-aware'] = x;
    }

    void xmlVersion(Object x) {
        this.advancedOptions['xml-version'] = x;
    }

    void xqueryAllowUpdate(Object x) {
        this.advancedOptions.xqueryAllowUpdate = x;
    }

    void xqueryConstructionMode(Object x) {
        this.advancedOptions.xqueryConstructionMode = x;
    }

    void xqueryDefaultElementNamespace(Object x) {
        this.advancedOptions.xqueryDefaultElementNamespace = x;
    }

    void xqueryDefaultFunctionNamespace(Object x) {
        this.advancedOptions.xqueryDefaultFunctionNamespace = x;
    }

    void xqueryEmptyLeast(Object x) {
        this.advancedOptions.xqueryEmptyLeast = x;
    }

    void xqueryInheritNamespaces(Object x) {
        this.advancedOptions.xqueryInheritNamespaces = x;
    }

    void xqueryMultipleModuleImports(Object x) {
        this.advancedOptions.xqueryMultipleModuleImports = x;
    }

    void xqueryPreserveBoundarySpace(Object x) {
        this.advancedOptions.xqueryPreserveBoundarySpace = x;
    }

    void xqueryPreserveNamespaces(Object x) {
        this.advancedOptions.xqueryPreserveNamespaces = x;
    }

    void xqueryRequiredContextItemType(Object x) {
        this.advancedOptions.xqueryRequiredContextItemType = x;
    }

    void xquerySchemaAware(Object x) {
        this.advancedOptions.xquerySchemaAware = x;
    }

    void xqueryStaticErrorListenerClass(Object x) {
        this.advancedOptions.xqueryStaticErrorListenerClass = x;
    }

    void xqueryVersion(Object x) {
        this.advancedOptions.xqueryVersion = x;
    }

    void xsdVersion(Object x) {
        this.advancedOptions['xsd-version'] = x;
    }

    void enableAssertions(Object x) {
        this.advancedOptions.enableAssertions = x;
    }

    void xsltSchemaAware(Object x) {
        this.advancedOptions.xsltSchemaAware = x;
    }

    void stylesheetErrorListener(Object x) {
        this.advancedOptions.stylesheetErrorListener = x;
    }

    void stylesheetURIResolver(Object x) {
        this.advancedOptions.stylesheetURIResolver = x;
    }

    void xsltVersion(Object x) {
        this.advancedOptions.xsltVersion = x;
    }

    // END ADVANCED OPTIONS

    void parameters(Map<String, String> parameters) {
        this.stylesheetParams = parameters
    }

    @Internal
    protected String getDefaultOutputExtension() {
        if (this.pluginOptions.outputFileExtension != null) {
            return this.pluginOptions.outputFileExtension
        }

        // Read output file extension from the <xsl:output> element of the
        // stylesheet.
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

    @SuppressWarnings('CatchException')
    protected FileCollection getIncludedStylesheets(File stylesheet) {
        def newCollection = []

        if (stylesheet != null) {
          newCollection += stylesheet

          try {
            GPathResult xslt = this.xmlSlurper.parse(stylesheet).declareNamespace(xsl: XSLT_NAMESPACE)

            (xslt.include + xslt.import).each { elem ->
              URI href = resolveUri(elem.@href[0].toString())
              URI uri = stylesheet.toURI().resolve(href)
              if (uri.getScheme() == "file") {
                newCollection += getIncludedStylesheets(new File(uri))
              }
            }
          } catch (FileNotFoundException ex) {
            // nevermind
          } catch (Exception ex) {
            logger.warn("Failed to parse: ${stylesheet}")
            logger.warn("  ${ex.getMessage()}")
          }

          return project.files(newCollection)
        }
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
        Path outputPath = project.file(this.options.output).toPath()

        if (this.pluginOptions.outputDirectoryLayout == 'nested' && filePath.startsWith(rootPath) && outputPath.startsWith(rootPath)) {
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
        } else if (project.files(this.options.input).size() == 1 && project.buildDir.getAbsolutePath() != this.options.output.getAbsolutePath()) {
            this.options.output
        } else {
            new File(this.options.output, getOutputFileName(file))
        }
    }

    @OutputFiles
    FileCollection getOutputFiles() {
        if (this.options.input != null) {
            project.files(project.files(this.options.input).collect { getOutputFile(it) })
        } else {
            project.files(this.options.output)
        }
    }

    @InputFiles
    @SkipWhenEmpty
    FileCollection getInputFiles() {
        FileCollection stylesheets = project.files() + getIncludedStylesheets(this.options.stylesheet)

        if (this.options.input != null) {
            project.files(this.options.input) + stylesheets
        } else {
            stylesheets
        }
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
        Map<String, String> commonOptions = this.options.findAll { name, value ->
            !['input', 'output'].contains(name)
        }.asImmutable()

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
            it.getClasspath().from(this.classpath);
        };

        List<String> commonArguments = getCommonArguments() + getStylesheetParameters()

        if (this.options.input != null) {
            project.files(this.options.input).each {
                List<String> arguments = getFileSpecificArguments(it) + commonArguments

                workQueue.submit(XsltTransformation) {
                    it.arguments.set(arguments)
                }
            }
        } else {
            String output = this.options.output.getPath()
            List<String> arguments = commonArguments + [makeSingleHyphenArgument(Argument.MAPPING.output, output)]

            workQueue.submit(XsltTransformation) {
                it.arguments.set(arguments)
            }
        }
    }
}
