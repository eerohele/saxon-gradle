package com.github.eerohele

@Singleton
class SaxonPluginConfigurations {
    // A singleton that holds the global configuration options.
    static final String DEFAULT = "SaxonPluginConfigurations.DEFAULT"

    protected final Map<String, Map<String,Object>> options = ["abc": [:]]
    protected final Map<String, Map<String,Object>> advancedOptions = [:]
    protected final Map<String, Map<String,Object>> pluginOptions = [:]

    void setOption(String name, String property, Object value) {
        if (name !in options) {
            options[name] = [:]
        }
        options[name][property] = value
    }

    void setAdvancedOption(String name, String property, Object value) {
        if (name !in advancedOptions) {
            advancedOptions[name] = [:]
        }
        advancedOptions[name][property] = value
    }

    void setPluginOption(String name, String property, Object value) {
        if (name !in pluginOptions) {
            pluginOptions[name] = [:]
        }
        pluginOptions[name][property] = value
    }
    
    Map<String,Object> getOptions() {
        return getOptions(DEFAULT)
    }

    Map<String,Object> getOptions(String name) {
        name in options ? options[name] : [:]
    }

    Map<String,Object> getAdvancedOptions() {
        return getAdvancedOptions(DEFAULT)
    }

    Map<String,Object> getAdvancedOptions(String name) {
        name in advancedOptions ? advancedOptions[name] : [:]
    }

    Map<String,Object> getPluginOptions() {
        return getPluginOptions(DEFAULT)
    }

    Map<String,Object> getPluginOptions(String name) {
        name in pluginOptions ? pluginOptions[name] : [:]
    }

    Boolean knownConfiguration(String name) {
        return name in options || name in advancedOptions || name in pluginOptions
    }
}

class SaxonPluginConfiguration implements SaxonPluginOptions {
    private String configname
    SaxonPluginConfiguration(String name) {
        configname = name
    }

    void setOption(String name, Object value) {
        SaxonPluginConfigurations.instance.setOption(configname, name, value)
    }

    void setAdvancedOption(String name, Object value) {
        SaxonPluginConfigurations.instance.setAdvancedOption(configname, name, value)
    }

    void setPluginOption(String name, Object value) {
        SaxonPluginConfigurations.instance.setPluginOption(configname, name, value)
    }
}
