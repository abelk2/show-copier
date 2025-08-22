package eu.abelk.showcopier.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.logger(): Logger =
    LoggerFactory.getLogger(T::class.java)

fun logger(name: String): Logger =
    LoggerFactory.getLogger(name)
