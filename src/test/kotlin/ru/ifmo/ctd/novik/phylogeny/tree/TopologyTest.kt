package ru.ifmo.ctd.novik.phylogeny.tree

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.utils.*

/**
 * @author Dmitry Novik ITMO University
 */
internal class TopologyTest : AbstractTestWithFile() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".topology"

    @Test
    fun `topology computation`() {
        runTests {
            val model = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
            val phylogeneticTree = model.evaluateSimpleData(this).cluster
            phylogeneticTree.topology().toGraphviz()
        }
    }
}