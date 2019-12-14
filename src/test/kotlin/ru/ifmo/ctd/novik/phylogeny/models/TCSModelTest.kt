package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.SimpleClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz

internal class TCSModelTest : AbstractTestWithFile() {
    override val testDirectory: String = "samples"

    @Test
    fun test() {
        runTests {
            val distanceEvaluator =
                    SimpleClusterDistanceEvaluator(CachingTaxonDistanceEvaluator(SimpleTaxonDistanceEvaluator()))
            val model = TCSModel(distanceEvaluator)
            val phylogeneticTree = model.evaluateSimpleData(this)
            phylogeneticTree.toGraphviz()
        }
    }

}