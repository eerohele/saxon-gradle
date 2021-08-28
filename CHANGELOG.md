# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## 0.9.0 - UNRELEASED
- Allow users to specify the Saxon version to use
- Use Gradle Worker API to prevent classpath conflicts
- Add support for outputDirectoryLayout property #18
- Do not require stylesheet to exist in configuration phase #19 (thanks @ndw!)
- Fix race condition in `getIncludedStylesheets` #20 (thanks @ndw!)
- Fix compatibility with Gradle 7

### 0.8.0 – 2019–08-02
- Add support for outputFileExtension property #6

### 0.7.0 – 2018-10-26
- Fix Saxon dependency #16

### 0.6.0 - 2018-02-07
- Add support for no-input transformations #11

### 0.5.0 - 2018-02-02
- Add support for Saxon's advanced options #10

### 0.4.1 - 2018-01-16
- Fix output directory for multiple input files #9

### 0.4.0 - 2017-10-31
- Always use latest Saxon-HE version when releasing new version of the plugin

### 0.3.0 - 2017-06-22
- Update to Saxon-HE 9.8.0.2

### 0.2.1 - 2016-11-25
- Fix NPE when stylesheet includes stylesheets whose URIs don't have catalog entries

### 0.2.0 - 2016-11-25
- Fix support for input file names with multiple periods
- Include stylesheets included with `<xsl:include>` or `<xsl:import>` in up-to-date check
- Fix critical plugin parameter mapping issue #5
- Update to Saxon 9.7.0.11

### 0.1.5 - 2016-11-25
- Unreleased

### 0.1.4 - 2016-04-07
- Fix support for stylesheets with DOCTYPE declarations #4

### 0.1.3 - 2016-04-06
- Fix Saxon config file path resolution error #1

### 0.1.2 - 2016-04-06
- Fix subproject stylesheet path resolution error #1

### 0.1.1 — 2016-04-04
- Fix error with using "output" option

### 0.1.0 — 2016-04-02
- Initial release
