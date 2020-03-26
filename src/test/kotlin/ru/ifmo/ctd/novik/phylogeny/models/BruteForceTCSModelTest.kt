package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.RealClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.PrimaryTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz

/**
 * @author Dmitry Novik ITMO University
 */
internal class BruteForceTCSModelTest : AbstractModelTest() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".bf"
    override val phylogeneticModel: PhylogeneticModel = PhylogeneticModel.BRUTE_FORCE_TCS
}