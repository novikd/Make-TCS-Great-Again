package ru.ifmo.ctd.novik.phylogeny

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.SimpleClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.models.TCSModel

/**
 * @author Novik Dmitry ITMO University
 */
fun main() {
    val taxonList: List<Taxon> = listOf(
        Taxon(0, "AA"),
        Taxon(1, "AG"),
        Taxon(2, "AC")
    )

    val distanceEvaluator =
        SimpleClusterDistanceEvaluator(CachingTaxonDistanceEvaluator(SimpleTaxonDistanceEvaluator()))
    val model = TCSModel(distanceEvaluator)

    val phylogeneticTree = model.computeTree(taxonList)
    println(phylogeneticTree)
}