package com.github.eerohele

import java.lang.reflect.Modifier

import net.sf.saxon.lib.FeatureKeys

class Argument {
    private static getStaticFields(Class c) {
        c.getDeclaredFields().findAll {
            Modifier.isStatic(it.getModifiers())
        }
    }

    // Collect a list of Saxon feature keys. For internal use only.
    @SuppressWarnings('UnusedPrivateField')
    private static final List<String> FEATUREKEYS =
            getStaticFields(FeatureKeys.class).collect {
                Object value = it.get(null)

                if (value != null) {
                    new URI(value.toString()).getPath().substring(9)
                }
            }

    // A map from plugin arguments to Saxon command-line options.
    //
    // See http://www.saxonica.com/html/documentation/using-xsl/commandline.html
    public static final Map<String, String> MAPPING = [
            catalog                : 'catalog',
            collectionResolver     : 'cr',
            config                 : 'config',
            dtd                    : 'dtd',
            expand                 : 'expand',
            explain                : 'explain',
            initializer            : 'init',
            initialMode            : 'im',
            initialTemplate        : 'it',
            input                  : 's',
            lineNumbers            : 'l',
            messageReceiver        : 'm',
            output                 : 'o',
            sourceSaxParser        : 'x',
            stylesheet             : 'xsl',
            stylesheetSaxParser    : 'y',
            suppressJavaCalls      : 'ext',
            uriResolver            : 'r',
            useAssociatedStylesheet: 'a'
    ].asImmutable()
}
