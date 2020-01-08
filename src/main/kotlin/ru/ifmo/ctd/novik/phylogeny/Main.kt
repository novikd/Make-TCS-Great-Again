package ru.ifmo.ctd.novik.phylogeny

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.SimpleClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.CachingTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.SimpleTaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.models.BruteForceTCSModel
import ru.ifmo.ctd.novik.phylogeny.models.IModel
import ru.ifmo.ctd.novik.phylogeny.models.TCSModel
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import ru.ifmo.ctd.novik.phylogeny.utils.toGraphviz

/**
 * @author Novik Dmitry ITMO University
 */
fun main(args: Array<String>) {
    var model: IModel = TCSModel(SimpleClusterDistanceEvaluator(CachingTaxonDistanceEvaluator(SimpleTaxonDistanceEvaluator())))
    val parser = object : CliktCommand() {
        val modelName: String by option(help = "model to run").default("TCS")
        override fun run() {
            if (modelName == "BF")
                model = BruteForceTCSModel(SimpleClusterDistanceEvaluator(SimpleTaxonDistanceEvaluator()))
        }
    }
    parser.main(args)


    val dataFile = "input.txt"
    val phylogeneticTree = model.evaluateSimpleData(dataFile)

    println(phylogeneticTree.toGraphviz())
}