package ru.ifmo.ctd.novik.phylogeny.settings

import kotlin.random.Random

/**
 * @author Dmitry Novik ITMO University
 */
object GlobalExecutionSettings {
    var DUMP_LIKELIHOOD = false
    var DEBUG_ENABLED   = false
    var RANDOM          = Random(0)
}