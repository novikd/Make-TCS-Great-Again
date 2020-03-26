package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.RealClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.PrimaryTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz

internal class TCSModelTest : AbstractModelTest() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".base"
    override val phylogeneticModel: PhylogeneticModel = PhylogeneticModel.BASE_TCS
}