package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.utils.compress
import ru.ifmo.ctd.novik.phylogeny.utils.unify

internal class MCMCModelTest {
    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `Apply recombination`(compressData: Boolean) {
        val model = MCMCModel(listOf(46, 245, 364), 1_00)
        val reader = FastaInputTaxaReader()
        val rawTaxa = reader.readFile("samples/recombination.fas")
        val taxonList = if (compressData) rawTaxa.compress().unify() else rawTaxa.unify()
        model.computeTopology(taxonList)
    }
}