package com.github.eerohele

import org.gradle.api.provider.ListProperty
import org.gradle.workers.WorkParameters

interface XsltWorkParameters extends WorkParameters {
    ListProperty<String> getArguments()
}
