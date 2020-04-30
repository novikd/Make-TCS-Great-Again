package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel
import ru.ifmo.ctd.novik.phylogeny.utils.create

internal class MCMCModelTest {
    @Test
    fun `Apply recombination`() {
        val model = PhylogeneticModel.MCMC.create(listOf(46, 245, 364))
        val reader = FastaInputTaxaReader()
        val taxonList = reader.readFile("samples/recombination.fas").distinctBy { it.genome.primary }
        model.computeTopology(taxonList)
    }
}