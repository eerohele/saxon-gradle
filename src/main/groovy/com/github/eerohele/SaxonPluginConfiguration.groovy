package com.github.eerohele

@Singleton
class SaxonPluginConfiguration implements SaxonPluginOptions {
    // A singleton that holds the global configuration options.

    final Map<String, Object> options = [:]
    final Map<String, String> advancedOptions = [:]
    final Map<String, String> pluginOptions = [:]

    void setOption(String name, Object value) {
        options[name] = value
    }

    void setAdvancedOption(String name, Object value) {
        advancedOptions[name] = value
    }

    void setPluginOption(String name, Object value) {
        pluginOptions[name] = value
    }
}
