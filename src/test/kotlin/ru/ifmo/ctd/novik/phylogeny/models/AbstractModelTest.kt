package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithOutputFile
import ru.ifmo.ctd.novik.phylogeny.io.output.VerbosePrinter
import ru.ifmo.ctd.novik.phylogeny.utils.*
import kotlin.random.Random

/**
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractModelTest : AbstractTestWithOutputFile() {
    abstract val phylogeneticModel: PhylogeneticModel

    companion object {
        @BeforeAll @JvmStatic fun setUp() {
            GlobalRandom = Random(0)
        }
    }

    @Test
    fun test() {
        runTestsWithOutput {
            val model = phylogeneticModel.create()
            val phylogeneticTree = model.evaluateSimpleData(this).cluster
            phylogeneticTree.toGraphviz(VerbosePrinter())
        }
    }
}