package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.BeforeEach
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithOutputFile
import ru.ifmo.ctd.novik.phylogeny.io.output.VerbosePrinter
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel
import ru.ifmo.ctd.novik.phylogeny.utils.create
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz
import kotlin.random.Random

/**
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractModelTest : AbstractTestWithOutputFile() {
    abstract val phylogeneticModel: PhylogeneticModel

    @BeforeEach
    fun setUp() {
        GlobalExecutionSettings.RANDOM = Random(0)
    }

    override fun test(testCasePath: String): String {
        setUp()

        val model = phylogeneticModel.create()
        val phylogeneticTree = model.evaluateSimpleData(testCasePath).cluster
        return phylogeneticTree.toGraphviz(VerbosePrinter())
    }
}