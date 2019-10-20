package ru.ifmo.ctd.novik.phylogeny.tree.metric

import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import java.util.*

/**
 * @author Novik Dmitry ITMO University
 */
abstract class AbstractMergeMetric(
    private val taxonDistanceEvaluator: TaxonDistanceEvaluator
) : MergeMetric, TaxonDistanceEvaluator by taxonDistanceEvaluator {

    private fun computeGraphDistances(node: Node): Map<Node, Int> {
        val result = hashMapOf<Node, Int>()
        result[node] = 0

        val queue: Queue<Node> = ArrayDeque()
        queue.add(node)

        while (queue.isNotEmpty()) {
            val currentNode = queue.poll()
            val currentDistance = result[currentNode]!!
            for (neighbor in currentNode.neighbors) {

                if (!result.contains(neighbor)) {
                    result[neighbor] = currentDistance + 1
                    queue.add(neighbor)
                }
            }
        }

        return result.filterKeys { x -> x.isRealTaxon }
    }

    override fun compute(firstNode: Node, secondNode: Node, bridgeLength: Int): Int {
        var result = 0

        val firstDistances = computeGraphDistances(firstNode)
        val secondDistances = computeGraphDistances(secondNode)

        for ((firstClusterNode, firstDistance) in firstDistances) {
            for ((secondClusterNode, secondDistance) in secondDistances) {
                val graphDistance = firstDistance + bridgeLength + secondDistance
                val realDistance = evaluate(firstClusterNode.taxon, secondClusterNode.taxon)

                result += compareDistances(graphDistance, realDistance)
            }
        }

        return result
    }

    abstract fun compareDistances(graphDistance: Int, realDistance: Int): Int
}