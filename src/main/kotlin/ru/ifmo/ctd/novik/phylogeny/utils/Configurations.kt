package ru.ifmo.ctd.novik.phylogeny.utils

import kotlinx.cli.ArgType
import ru.ifmo.ctd.novik.phylogeny.common.Phylogeny
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.AbsoluteClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.RealClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.AbsoluteTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.PrimaryTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.toCaching
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.models.*
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import java.util.logging.Logger

var DEBUG_ENABLED = false

inline fun debug(action: () -> Unit) {
    if (DEBUG_ENABLED)
        action()
}

enum class PhylogeneticModel(val shortName: String) {
    BASE_TCS("baseTCS"),
    BRUTE_FORCE_TCS("bfTCS"),
    SET_BRUTE_FORCE_TCS("setBfTCS"),
    MCMC("mcmc");

    override fun toString(): String = shortName
}

fun PhylogeneticModel.create(hotspots: List<Int> = listOf()): IModel {
    return when (this) {
        PhylogeneticModel.BASE_TCS -> TCSModel(RealClusterDistanceEvaluator(PrimaryTaxonDistanceEvaluator().toCaching()))
        PhylogeneticModel.BRUTE_FORCE_TCS -> BruteForceTCSModel(RealClusterDistanceEvaluator(PrimaryTaxonDistanceEvaluator()))
        PhylogeneticModel.SET_BRUTE_FORCE_TCS -> SetBruteForceTCSModel(AbsoluteClusterDistanceEvaluator(AbsoluteTaxonDistanceEvaluator().toCaching()))
        PhylogeneticModel.MCMC -> MCMCModel(hotspots)
    }
}

object ModelChoice : ArgType<PhylogeneticModel>(true) {
    override val description: kotlin.String
        get() = "{ Value should be one of [${PhylogeneticModel.values().joinToString(separator = ", ")}] }"

    override val conversion: (value: kotlin.String, name: kotlin.String) -> PhylogeneticModel
        get() = { value, _ -> PhylogeneticModel.values().find { model -> model.shortName == value } ?: PhylogeneticModel.BASE_TCS }
}

fun String.readSimpleData(): List<Taxon> = SimpleInputTaxaReader().readFile(this)

fun IModel.evaluateSimpleData(dataFile: String): Phylogeny {
    val reader = SimpleInputTaxaReader()
    val taxonList = reader.readFile(dataFile)

    val phylogeny = this.computePhylogeny(taxonList)
    phylogeny.unify()
    return phylogeny
}

fun IModel.computeForFastaData(dataFile: String): RootedTopology {
    val reader = FastaInputTaxaReader()
    val taxonList = reader.readFile(dataFile).distinctBy { it.genome.primary }

    return this.computeTopology(taxonList)
}

inline fun <reified R : Any> R.logger(): Logger =
        Logger.getLogger(this::class.java.name)

