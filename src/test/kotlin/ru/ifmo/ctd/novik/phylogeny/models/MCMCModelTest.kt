package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.utils.prepareInputData
import ru.ifmo.ctd.novik.phylogeny.utils.resetGlobalSettings

internal class MCMCModelTest {
    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `Apply recombination`(compressionEnabled: Boolean) {
        resetGlobalSettings()
        GlobalExecutionSettings.COMPRESSION_ENABLED = compressionEnabled

        val model = MCMCModel(listOf(46, 245, 364), 1_00)
        val taxonList = prepareInputData(FastaInputTaxaReader(), "samples/recombination.fas")
        model.computeTopology(taxonList)
    }
}