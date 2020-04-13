package ru.ifmo.ctd.novik.phylogeny.utils

import kotlinx.cli.ArgType
import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.AbsoluteClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.RealClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.AbsoluteTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.PrimaryTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.models.BruteForceTCSModel
import ru.ifmo.ctd.novik.phylogeny.models.IModel
import ru.ifmo.ctd.novik.phylogeny.models.SetBruteForceTCSModel
import ru.ifmo.ctd.novik.phylogeny.models.TCSModel

enum class PhylogeneticModel(val shortName: String) {
    BASE_TCS("baseTCS"),
    BRUTE_FORCE_TCS("bfTCS"),
    SET_BRUTE_FORCE_TCS("setBfTCS");

    override fun toString(): String = shortName
}

class ModelCreationFailureException(model: PhylogeneticModel) : RuntimeException("Can't create $model model")

fun PhylogeneticModel.create(): IModel {
    return when (this) {
        PhylogeneticModel.BASE_TCS -> TCSModel(RealClusterDistanceEvaluator(CachingTaxonDistanceEvaluator(PrimaryTaxonDistanceEvaluator())))
        PhylogeneticModel.BRUTE_FORCE_TCS -> BruteForceTCSModel(RealClusterDistanceEvaluator(PrimaryTaxonDistanceEvaluator()))
        PhylogeneticModel.SET_BRUTE_FORCE_TCS -> SetBruteForceTCSModel(AbsoluteClusterDistanceEvaluator(AbsoluteTaxonDistanceEvaluator()))
        else -> throw ModelCreationFailureException(this)
    }
}

object ModelChoice : ArgType<PhylogeneticModel>(true) {
    override val description: kotlin.String
        get() = "{ Value should be one of [${PhylogeneticModel.values().joinToString(separator = ", ")}] }"

    override val conversion: (value: kotlin.String, name: kotlin.String) -> PhylogeneticModel
        get() = { value, _ -> PhylogeneticModel.values().find { model -> model.shortName == value } ?: PhylogeneticModel.BASE_TCS }
}

fun IModel.evaluateSimpleData(dataFile: String): Cluster {
    val reader = SimpleInputTaxaReader()
    val taxonList = reader.readFile(dataFile)

    return this.computeTree(taxonList).unify()
}