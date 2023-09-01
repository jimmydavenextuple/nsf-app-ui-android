package com.nextuple.nsf

import java.io.File
import java.util.*

fun getEnv(key: String): String? {
    val env = System.getenv(key)
    if (env != null) {
        return env
    }

    val file = File("nsf.properties").let {
        if (it.exists()) it else null
    } ?: return null

    val props = Properties().also {
        it.load(file.inputStream())
    }

    return props.getProperty(key) ?: null
}

fun getVersion(): String {
    val isLocal = getEnv("IS_LOCAL")?.lowercase()?.toBooleanStrictOrNull() ?: false

    return if (isLocal) {
        "${File("version").readText().trim()}-LOCAL"
    } else {
        getEnv("APK_VERSION").orEmpty()
    }
}