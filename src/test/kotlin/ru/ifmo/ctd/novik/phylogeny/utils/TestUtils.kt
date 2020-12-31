package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.RawGenomeFactory
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import kotlin.random.Random

fun resetGlobalSettings() {
    GlobalExecutionSettings.RANDOM = Random(0)
    GlobalExecutionSettings.COMPRESSION_ENABLED = false
    GlobalExecutionSettings.GENOME_FACTORY = RawGenomeFactory()
}