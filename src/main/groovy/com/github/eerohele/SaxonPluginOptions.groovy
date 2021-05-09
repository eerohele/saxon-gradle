package com.github.eerohele

// This trait is for initializing options and advanced options.
// It's used by both the global configuration singleton and
// each individual instance of a SaxonXsltTask.

@SuppressWarnings('MethodCount')
trait SaxonPluginOptions {
    abstract void setOption(String name, Object value)
    abstract void setAdvancedOption(String name, Object value)
    abstract void setPluginOption(String name, Object value)

    // ============================================================

    void classpath(Object cp) {
        setPluginOption('classpath', cp)
    }

    void outputFileExtension(String extension) {
        setPluginOption('outputFileExtension', extension)
    }

    void outputDirectoryLayout(String layout) {
        setPluginOption('outputDirectoryLayout', layout)
    }

    // ============================================================

    void config(Object config) {
        setOption('config', project.file(config))
    }

    void collectionResolver(String resolver) {
        setOption('collectionResolver', resolver)
    }

    void catalog(Object catalog) {
        setOption('catalog', catalog in List ? catalog.join(";") : catalog.toString())
    }

    void dtd(Object dtd) {
        setOption('dtd', dtd)
    }

    void expand(Object expand) {
        setOption('expand', expand)
    }

    void explain(Object explain) {
        setOption('explain', explain)
    }

    void initializer(String initializer) {
        setOption('initializer', initializer)
    }

    void initialMode(String initialMode) {
        setOption('initialMode', initialMode)
    }

    void initialTemplate(String initialTemplate) {
        setOption('initialTemplate', initialTemplate)
    }

    void lineNumbers(Object lineNumbers) {
        setOption('lineNumbers', lineNumbers)
    }

    void messageReceiver(String receiver) {
        setOption('messageReceiver', receiver)
    }

    void sourceSaxParser(String parser) {
        setOption('sourceSaxParser', parser)
    }

    void stylesheetSaxParser(String parser) {
        setOption('stylesheetSaxParser', parser)
    }

    void suppressJavaCalls(Object suppress) {
        setOption('suppressJavaCalls', suppress)
    }

    void uriResolver(Object resolver) {
        setOption('uriResolver', resolver)
    }

    void useAssociatedStylesheet(Object use) {
        setOption('useAssociatedStylesheet', use)
    }

    // START ADVANCED OPTIONS

    void allowExternalFunctions(Boolean x) {
        setAdvancedOption('allow-external-functions', x)
    }

    void allowMultithreading(Boolean x) {
        setAdvancedOption('allow-multithreading', x)
    }

    void allowOldJavaURIFormat(Boolean x) {
        setAdvancedOption('allow-old-java-uri-format', x)
    }

    void allowSyntaxExtensions(Boolean x) {
        setAdvancedOption('allowSyntaxExtensions', x)
    }

    void assertionsCanSeeComments(Boolean x) {
        setAdvancedOption('assertionsCanSeeComments', x)
    }

    void collationUriResolver(Object x) {
        setAdvancedOption('collation-uri-resolver', x)
    }

    void collationUriResolverClass(Object x) {
        setAdvancedOption('collation-uri-resolver-class', x)
    }

    void collectionFinder(Object x) {
        setAdvancedOption('collection-finder', x)
    }

    void collectionFinderClass(Object x) {
        setAdvancedOption('collection-finder-class', x)
    }

    void collectionUriResolver(Object x) {
        setAdvancedOption('collection-uri-resolver', x)
    }

    void collectionUriResolverClass(Object x) {
        setAdvancedOption('collection-uri-resolver-class', x)
    }

    void compileWithTracing(Boolean x) {
        setAdvancedOption('with', x)
    }

    void configuration(Object x) {
        setAdvancedOption('configuration', x)
    }

    void configurationFile(Object x) {
        setAdvancedOption('configuration-file', x)
    }

    void debugByteCode(Object x) {
        setAdvancedOption('debugByteCode', x)
    }

    void debugByteCodeDir(Object x) {
        setAdvancedOption('debugByteCodeDir', x)
    }

    void defaultCollation(Object x) {
        setAdvancedOption('defaultCollation', x)
    }

    void defaultCollection(Object x) {
        setAdvancedOption('defaultCollection', x)
    }

    void defaultCountry(Object x) {
        setAdvancedOption('defaultCountry', x)
    }

    void defaultLanguage(Object x) {
        setAdvancedOption('defaultLanguage', x)
    }

    void defaultRegexEngine(Object x) {
        setAdvancedOption('defaultRegexEngine', x)
    }

    void disableXslEvaluate(Boolean x) {
        setAdvancedOption('disableXslEvaluate', x)
    }

    void displayByteCode(Boolean x) {
        setAdvancedOption('displayByteCode', x)
    }

    void validation(Object x) {
        setAdvancedOption('validation', x)
    }

    void dtdValidationRecoverable(Object x) {
        setAdvancedOption('dtd-validation-recoverable', x)
    }

    void eagerEvaluation(Object x) {
        setAdvancedOption('eagerEvaluation', x)
    }

    void entityResolverClass(Object x) {
        setAdvancedOption('entityResolverClass', x)
    }

    void environmentVariableResolver(Object x) {
        setAdvancedOption('environmentVariableResolver', x)
    }

    void environmentVariableResolverClass(Object x) {
        setAdvancedOption('environmentVariableResolverClass', x)
    }

    void errorListenerClass(Object x) {
        setAdvancedOption('errorListenerClass', x)
    }

    void expathFileDeleteTemporaryFiles(Object x) {
        setAdvancedOption('expathFileDeleteTemporaryFiles', x)
    }

    void generateByteCode(Object x) {
        setAdvancedOption('generateByteCode', x)
    }

    void ignoreSAXSourceParser(Object x) {
        setAdvancedOption('ignoreSAXSourceParser', x)
    }

    void implicitSchemaImports(Object x) {
        setAdvancedOption('implicitSchemaImports', x)
    }

    void lazyConstructionMode(Object x) {
        setAdvancedOption('lazyConstructionMode', x)
    }

    void licenseFileLocation(Object x) {
        setAdvancedOption('licenseFileLocation', x)
    }

    void markDefaultedAttributes(Object x) {
        setAdvancedOption('markDefaultedAttributes', x)
    }

    void maxCompiledClasses(Object x) {
        setAdvancedOption('maxCompiledClasses', x)
    }

    void messageEmitterClass(Object x) {
        setAdvancedOption('messageEmitterClass', x)
    }

    void moduleURIResolver(Object x) {
        setAdvancedOption('moduleURIResolver', x)
    }

    void moduleURIResolverClass(Object x) {
        setAdvancedOption('moduleURIResolverClass', x)
    }

    void monitorHotSpotByteCode(Object x) {
        setAdvancedOption('monitorHotSpotByteCode', x)
    }

    void multipleSchemaImports(Object x) {
        setAdvancedOption('multipleSchemaImports', x)
    }

    void namePool(Object x) {
        setAdvancedOption('namePool', x)
    }

    void occurrenceLimits(Object x) {
        setAdvancedOption('occurrenceLimits', x)
    }

    void optimizationLevel(Object x) {
        setAdvancedOption('optimizationLevel', x)
    }

    void outputURIResolver(Object x) {
        setAdvancedOption('outputURIResolver', x)
    }

    void outputURIResolverClass(Object x) {
        setAdvancedOption('outputURIResolverClass', x)
    }

    void parserFeature(Object x) {
        setAdvancedOption('parserFeature', x)
    }

    void parserProperty(Object x) {
        setAdvancedOption('parserProperty', x)
    }

    void preEvaluateDocFunction(Object x) {
        setAdvancedOption('preEvaluateDocFunction', x)
    }

    void preferJaxpParser(Object x) {
        setAdvancedOption('preferJaxpParser', x)
    }

    void recognizeUriQueryParameters(Object x) {
        setAdvancedOption('recognize-uri-query-parameters', x)
    }

    void recoveryPolicy(Object x) {
        setAdvancedOption('recoveryPolicy', x)
    }

    void recoveryPolicyName(Object x) {
        setAdvancedOption('recoveryPolicyName', x)
    }

    void resultDocumentThreads(Object x) {
        setAdvancedOption('resultDocumentThreads', x)
    }

    void retainDtdAttributeTypes(Object x) {
        setAdvancedOption('retain-dtd-attribute-types', x)
    }

    void schemaURIResolver(Object x) {
        setAdvancedOption('schemaURIResolver', x)
    }

    void schemaURIResolverClass(Object x) {
        setAdvancedOption('schemaURIResolverClass', x)
    }

    void schemaValidation(Object x) {
        setAdvancedOption('schema-validation', x)
    }

    void schemaValidationMode(Object x) {
        setAdvancedOption('schema-validation-mode', x)
    }

    void serializerFactoryClass(Object x) {
        setAdvancedOption('serializerFactoryClass', x)
    }

    void sourceParserClass(Object x) {
        setAdvancedOption('sourceParserClass', x)
    }

    void sourceResolverClass(Object x) {
        setAdvancedOption('sourceResolverClass', x)
    }

    void stableCollectionUri(Object x) {
        setAdvancedOption('stableCollectionUri', x)
    }

    void stableUnparsedText(Object x) {
        setAdvancedOption('stableUnparsedText', x)
    }

    void standardErrorOutputFile(Object x) {
        setAdvancedOption('standardErrorOutputFile', x)
    }

    void streamability(Object x) {
        setAdvancedOption('streamability', x)
    }

    void strictStreamability(Object x) {
        setAdvancedOption('strictStreamability', x)
    }

    void streamingFallback(Object x) {
        setAdvancedOption('streamingFallback', x)
    }

    void stripWhitespace(Object x) {
        setAdvancedOption('strip-whitespace', x)
    }

    void styleParserClass(Object x) {
        setAdvancedOption('styleParserClass', x)
    }

    void suppressEvaluationExpiryWarning(Object x) {
        setAdvancedOption('suppressEvaluationExpiryWarning', x)
    }

    void suppressXPathWarnings(Object x) {
        setAdvancedOption('suppressXPathWarnings', x)
    }

    void suppressXsltNamespaceCheck(Object x) {
        setAdvancedOption('suppressXsltNamespaceCheck', x)
    }

    void thresholdForCompilingTypes(Object x) {
        setAdvancedOption('thresholdForCompilingTypes', x)
    }

    void timing(Object x) {
        setAdvancedOption('timing', x)
    }

    void traceExternalFunctions(Object x) {
        setAdvancedOption('trace-external-functions', x)
    }

    void traceListener(Object x) {
        setAdvancedOption('traceListener', x)
    }

    void traceListenerClass(Object x) {
        setAdvancedOption('traceListenerClass', x)
    }

    void traceListenerOutputFile(Object x) {
        setAdvancedOption('traceListenerOutputFile', x)
    }

    void traceOptimizerDecisions(Object x) {
        setAdvancedOption('trace-optimizer-decisions', x)
    }

    void treeModel(Object x) {
        setAdvancedOption('treeModel', x)
    }

    void treeModelName(Object x) {
        setAdvancedOption('treeModelName', x)
    }

    void unparsedTextURIResolver(Object x) {
        setAdvancedOption('unparsedTextURIResolver', x)
    }

    void unparsedTextURIResolverClass(Object x) {
        setAdvancedOption('unparsedTextURIResolverClass', x)
    }

    void uriResolverClass(Object x) {
        setAdvancedOption('uriResolverClass', x)
    }

    void usePiDisableOutputEscaping(Object x) {
        setAdvancedOption('use-pi-disable-output-escaping', x)
    }

    void useTypedValueCache(Object x) {
        setAdvancedOption('use-typed-value-cache', x)
    }

    void useXsiSchemaLocation(Object x) {
        setAdvancedOption('useXsiSchemaLocation', x)
    }

    void validationComments(Object x) {
        setAdvancedOption('validation-comments', x)
    }

    void validationWarnings(Object x) {
        setAdvancedOption('validation-warnings', x)
    }

    void versionWarning(Object x) {
        setAdvancedOption('version-warning', x)
    }

    void xincludeAware(Object x) {
        setAdvancedOption('xinclude-aware', x)
    }

    void xmlVersion(Object x) {
        setAdvancedOption('xml-version', x)
    }

    void xqueryAllowUpdate(Object x) {
        setAdvancedOption('xqueryAllowUpdate', x)
    }

    void xqueryConstructionMode(Object x) {
        setAdvancedOption('xqueryConstructionMode', x)
    }

    void xqueryDefaultElementNamespace(Object x) {
        setAdvancedOption('xqueryDefaultElementNamespace', x)
    }

    void xqueryDefaultFunctionNamespace(Object x) {
        setAdvancedOption('xqueryDefaultFunctionNamespace', x)
    }

    void xqueryEmptyLeast(Object x) {
        setAdvancedOption('xqueryEmptyLeast', x)
    }

    void xqueryInheritNamespaces(Object x) {
        setAdvancedOption('xqueryInheritNamespaces', x)
    }

    void xqueryMultipleModuleImports(Object x) {
        setAdvancedOption('xqueryMultipleModuleImports', x)
    }

    void xqueryPreserveBoundarySpace(Object x) {
        setAdvancedOption('xqueryPreserveBoundarySpace', x)
    }

    void xqueryPreserveNamespaces(Object x) {
        setAdvancedOption('xqueryPreserveNamespaces', x)
    }

    void xqueryRequiredContextItemType(Object x) {
        setAdvancedOption('xqueryRequiredContextItemType', x)
    }

    void xquerySchemaAware(Object x) {
        setAdvancedOption('xquerySchemaAware', x)
    }

    void xqueryStaticErrorListenerClass(Object x) {
        setAdvancedOption('xqueryStaticErrorListenerClass', x)
    }

    void xqueryVersion(Object x) {
        setAdvancedOption('xqueryVersion', x)
    }

    void xsdVersion(Object x) {
        setAdvancedOption('xsd-version', x)
    }

    void enableAssertions(Object x) {
        setAdvancedOption('enableAssertions', x)
    }

    void xsltSchemaAware(Object x) {
        setAdvancedOption('xsltSchemaAware', x)
    }

    void stylesheetErrorListener(Object x) {
        setAdvancedOption('stylesheetErrorListener', x)
    }

    void stylesheetURIResolver(Object x) {
        setAdvancedOption('stylesheetURIResolver', x)
    }

    void xsltVersion(Object x) {
        setAdvancedOption('xsltVersion', x)
    }
}
