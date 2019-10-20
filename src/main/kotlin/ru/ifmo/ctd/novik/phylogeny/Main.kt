package ru.ifmo.ctd.novik.phylogeny

import ru.ifmo.ctd.novik.phylogeny.distance.cluster.SimpleClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.models.TCSModel

/**
 * @author Novik Dmitry ITMO University
 */
fun main(args: Array<String>) {
    val reader = SimpleInputTaxaReader()
    val taxonList = reader.readFile(if (args.size > 1) args[1] else "samples/sample01.txt")

    val distanceEvaluator =
        SimpleClusterDistanceEvaluator(CachingTaxonDistanceEvaluator(SimpleTaxonDistanceEvaluator()))
    val model = TCSModel(distanceEvaluator)

    val phylogeneticTree = model.computeTree(taxonList)
    val printer = GraphvizOutputClusterVisitor()
    println(printer.visit(phylogeneticTree))
}