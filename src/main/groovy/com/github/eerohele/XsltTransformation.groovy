package com.github.eerohele

import net.sf.saxon.Transform
import org.gradle.workers.WorkAction

@SuppressWarnings('AbstractClassWithoutAbstractMethod')
abstract class XsltTransformation implements WorkAction<XsltWorkParameters> {
    @Override
    void execute() {
        // println("Using Saxon version ${net.sf.saxon.Version.productVersion}")
        // println("Transform with ${parameters.arguments.get() as String[]}")
        new Transform().doTransform(parameters.arguments.get() as String[], '')
    }
}
