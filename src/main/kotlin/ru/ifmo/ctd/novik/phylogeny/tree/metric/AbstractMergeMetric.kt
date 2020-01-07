package ru.ifmo.ctd.novik.phylogeny.tree.metric

import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingMetaData
import ru.ifmo.ctd.novik.phylogeny.utils.computeGraphDistances
import java.util.*

/**
 * @author Novik Dmitry ITMO University
 */
abstract class AbstractMergeMetric(
    private val taxonDistanceEvaluator: TaxonDistanceEvaluator
) : MergeMetric, TaxonDistanceEvaluator by taxonDistanceEvaluator {

    override fun compute(firstNode: Node, secondNode: Node, metaData: MergingMetaData): Int {
        var result = 0

        val firstDistances = firstNode.computeGraphDistances()
        val secondDistances = secondNode.computeGraphDistances()

        for ((firstClusterNode, firstDistance) in firstDistances) {
            for ((secondClusterNode, secondDistance) in secondDistances) {
                val graphDistance = firstDistance + metaData.bridgeLength + secondDistance
                val realDistance = evaluate(firstClusterNode.taxon, secondClusterNode.taxon)

                result += compareDistances(graphDistance, realDistance)
            }
        }

        return result
    }

    abstract fun compareDistances(graphDistance: Int, realDistance: Int): Int
}