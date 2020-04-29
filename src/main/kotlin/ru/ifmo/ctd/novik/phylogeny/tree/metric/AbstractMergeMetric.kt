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

    private val cache = mutableMapOf<Node, Map<Node, Int>>()

    override fun compute(firstNode: Node, secondNode: Node, metaData: MergingMetaData): Int {
        var result = 0

        val firstDistances = cache.computeIfAbsent(firstNode) { it.computeGraphDistances() }
        val secondDistances = cache.computeIfAbsent(secondNode) { it.computeGraphDistances() }

        for ((firstClusterNode, firstDistance) in firstDistances) {
            for ((secondClusterNode, secondDistance) in secondDistances) {
                val graphDistance = firstDistance + metaData.bridgeLength + secondDistance
                val realDistance = evaluate(firstClusterNode.taxon, secondClusterNode.taxon)

                result += compareDistances(graphDistance, realDistance.value)
            }
        }

        return result
    }

    abstract fun compareDistances(graphDistance: Int, realDistance: Int): Int
}