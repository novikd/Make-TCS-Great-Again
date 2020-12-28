package ru.ifmo.ctd.novik.phylogeny.network

import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithOutputFile
import ru.ifmo.ctd.novik.phylogeny.io.output.VerbosePrinter
import ru.ifmo.ctd.novik.phylogeny.utils.*

/**
 * @author Dmitry Novik ITMO University
 */
internal class TopologyTest : AbstractTestWithOutputFile() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".topology"

    override fun test(testCasePath: String): String {
        val model = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
        val phylogeneticTree = model.evaluateSimpleData(testCasePath).cluster
        return phylogeneticTree.topology().toGraphviz(VerbosePrinter())
    }
}