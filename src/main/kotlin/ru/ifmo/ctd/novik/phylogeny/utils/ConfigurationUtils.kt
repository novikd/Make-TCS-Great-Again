package ru.ifmo.ctd.novik.phylogeny.utils

import kotlinx.cli.ArgType
import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.SimpleClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.models.BruteForceTCSModel
import ru.ifmo.ctd.novik.phylogeny.models.IModel
import ru.ifmo.ctd.novik.phylogeny.models.TCSModel

enum class PhylogeneticModel(val shortName: String) {
    BASE_TCS("baseTCS"),
    BRUTE_FORCE_TCS("bfTCS");

    override fun toString(): String = shortName
}

class ModelCreationFailureException(model: PhylogeneticModel) : RuntimeException("Can't create $model model")

fun PhylogeneticModel.create(): IModel {
    return when (this) {
        PhylogeneticModel.BASE_TCS -> TCSModel(SimpleClusterDistanceEvaluator(CachingTaxonDistanceEvaluator(SimpleTaxonDistanceEvaluator())))
        PhylogeneticModel.BRUTE_FORCE_TCS -> BruteForceTCSModel(SimpleClusterDistanceEvaluator(SimpleTaxonDistanceEvaluator()))
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