package ru.ifmo.ctd.novik.phylogeny

import ru.ifmo.ctd.novik.phylogeny.distance.cluster.SimpleClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.models.TCSModel
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz

/**
 * @author Novik Dmitry ITMO University
 */
fun main(args: Array<String>) {
    val dataFile = if (args.size > 1) args[1] else "samples/sample02.txt"

    val distanceEvaluator =
        SimpleClusterDistanceEvaluator(CachingTaxonDistanceEvaluator(SimpleTaxonDistanceEvaluator()))
    val model = TCSModel(distanceEvaluator)
    val phylogeneticTree = model.evaluateSimpleData(dataFile)

    println(phylogeneticTree.toGraphviz())
}