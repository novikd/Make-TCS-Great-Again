package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.SimpleClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz

/**
 * @author Dmitry Novik ITMO University
 */
internal class BruteForceTCSModelTest : AbstractTestWithFile() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".bf"

    @Test
    fun test() {
        runTests {
            val distanceEvaluator =
                    SimpleClusterDistanceEvaluator(SimpleTaxonDistanceEvaluator())
            val model = BruteForceTCSModel(distanceEvaluator)
            val phylogeneticTree = model.evaluateSimpleData(this)
            phylogeneticTree.toGraphviz()
        }
    }
}