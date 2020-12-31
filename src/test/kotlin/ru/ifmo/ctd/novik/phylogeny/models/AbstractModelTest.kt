package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithOutputFile
import ru.ifmo.ctd.novik.phylogeny.io.output.VerbosePrinter
import ru.ifmo.ctd.novik.phylogeny.utils.*

/**
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractModelTest : AbstractTestWithOutputFile() {
    abstract val phylogeneticModel: PhylogeneticModel

    override fun test(testCasePath: String): String {
        resetGlobalSettings()

        val model = phylogeneticModel.create()
        val phylogeneticTree = model.evaluateSimpleData(testCasePath).cluster
        return phylogeneticTree.toGraphviz(VerbosePrinter())
    }
}