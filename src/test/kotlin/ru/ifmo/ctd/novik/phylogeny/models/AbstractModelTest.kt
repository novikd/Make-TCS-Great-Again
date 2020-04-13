package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel
import ru.ifmo.ctd.novik.phylogeny.utils.create
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz

/**
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractModelTest : AbstractTestWithFile() {
    abstract val phylogeneticModel: PhylogeneticModel

    @Test
    fun test() {
        runTests {
            val model = phylogeneticModel.create()
            val phylogeneticTree = model.evaluateSimpleData(this).cluster
            phylogeneticTree.toGraphviz()
        }
    }
}