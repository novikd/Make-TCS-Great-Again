package ru.ifmo.ctd.novik.phylogeny.settings

import ru.ifmo.ctd.novik.phylogeny.common.GenomeFactory
import ru.ifmo.ctd.novik.phylogeny.common.RawGenomeFactory
import kotlin.random.Random

/**
 * @author Dmitry Novik ITMO University
 */
object GlobalExecutionSettings {
    var DUMP_LIKELIHOOD                = false
    var DEBUG_ENABLED                  = false
    var RANDOM                         = Random(0)
    var COMPRESSION_ENABLED            = false
    var GENOME_FACTORY : GenomeFactory = RawGenomeFactory()
}