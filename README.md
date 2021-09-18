Saxon Gradle Plugin
===================

A plugin for running [Saxon][saxon] from [Gradle][gradle].

## Example

```groovy
repositories {
    mavenCentral()
}

configurations {
    saxon
}

dependencies {
    // Use Saxon-HE 9.9.1-5 instead of the Saxon-HE version that comes with the
    // plugin. 
    saxon 'net.sf.saxon:Saxon-HE:9.9.1-5'
}

plugins {
    id 'com.github.eerohele.saxon-gradle' version '0.9.0-beta4'
}

xslt {
    // Use the Saxon version you specified in the "dependencies" block.
    classpath.from(configurations.saxon)

    stylesheet 'stylesheet.xsl'

    // Transform every .xml file in the "input" directory.
    input fileTree(dir: 'input', include: '*.xml')

    // Set Saxon configuration file.
    config 'config/saxon.xml'

    // Set XSLT parameters.
    parameters(
        debug: true,
        version: project.version
    )
}
```

See the `examples` directory in this repository for additional examples.

## Benefits

- A nice and clean syntax for running XSLT transformations with Saxon.
- Better performance via the [Gradle Daemon][gradle-daemon].
- Easily configure Saxon either in the Gradle buildfile or via a
  [Saxon configuration file][saxon-config-file].
- Only rerun the transformation if the input file(s) or the stylesheet has
  changed (or if forced with `--rerun-tasks`).
- Rapid XSLT development via Gradle's `--continuous` option: automatically
  run your stylesheet every time it or your input file changes.
- Transform Gradle [file collections][gradle-file-collections].
- Use XSLT 3.0 via Saxon-HE 9.8 (or any subsequent Saxon version you need).

## Options

The plugin supports all of the same options as
[the Saxon command-line tool][saxon-command-line], but with more readable names:

```
catalog
collectionResolver
config
dtd
expand
explain
initializer
initialMode
initialTemplate
input
lineNumbers
messageReceiver
output
sourceSaxParser
stylesheet
stylesheetSaxParser
suppressJavaCalls
uriResolver
uriSources
useAssociatedStylesheet
```

The plugin also supports Saxon's advanced options. For a full list of the available advanced options, see the bits
between `// START ADVANCED OPTIONS` and `// END ADVANCED OPTIONS` in
[`SaxonXsltTask.groovy`](https://github.com/eerohele/saxon-gradle/blob/master/src/main/groovy/com/github/eerohele/SaxonXsltTask.groovy).

## Plugin options

Additionally, the plugin supports these options that aren't related to Saxon:

- `outputFileExtension` — specify the file extension you want to use for output files rather than have it be deduced from the XSLT output method
- `outputDirectoryLayout` — add `outputDirectoryLayout 'nested'` to your `xslt` task if you want your output directory layout to mirror your input directory layout (only if both your input and output directories are under the project base directory)

## Limitations
- Currently only supports XSLT. If you need XQuery support, please open an issue
  and I'll see what I can do.

[gradle]: http://gradle.org/
[gradle-daemon]: https://docs.gradle.org/current/userguide/gradle_daemon.html
[gradle-file-collections]: https://docs.gradle.org/current/userguide/working_with_files.html#sec:file_collections
[saxon]: http://saxonica.com/
[saxon-command-line]: http://www.saxonica.com/html/documentation/using-xsl/commandline/
[saxon-config-file]: http://saxonica.com/html/documentation/configuration/configuration-file
