package com.github.eerohele

trait OptionsConfig {
    private final Map<String, Object> options = [:]
    private final Map<String, String> advancedOptions = [:]

    Map<String, Object> configuredOptions() {
        return options
    }

    Map<String, String> configuredAdvancedOptions() {
        return advancedOptions
    }

    void config(Object config) {
        this.options.config = project.file(config)
    }

    void collectionResolver(String resolver) {
        this.options.collectionResolver = resolver
    }

    void catalog(Object catalog) {
        this.options.catalog = catalog
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

    void lineNumbers(Object lineNumbers) {
        this.options.lineNumbers = lineNumbers
    }

    void messageReceiver(String receiver) {
        this.options.messageReceiver = receiver
    }

    void sourceSaxParser(String parser) {
        this.options.sourceSaxParser = parser
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
}
