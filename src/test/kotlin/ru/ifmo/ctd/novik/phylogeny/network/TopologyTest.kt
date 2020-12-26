package ru.ifmo.ctd.novik.phylogeny.network

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithOutputFile
import ru.ifmo.ctd.novik.phylogeny.io.output.VerbosePrinter
import ru.ifmo.ctd.novik.phylogeny.utils.*

/**
 * @author Dmitry Novik ITMO University
 */
internal class TopologyTest : AbstractTestWithOutputFile() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".topology"

    @Test
    fun `topology computation`() {
        runTestsWithOutput {
            val model = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
            val phylogeneticTree = model.evaluateSimpleData(this).cluster
            phylogeneticTree.topology().toGraphviz(VerbosePrinter())
        }
    }
}