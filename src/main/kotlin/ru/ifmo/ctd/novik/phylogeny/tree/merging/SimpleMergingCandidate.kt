package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleMergingCandidate(
    override val distance: Int,
    override val firstCandidate: Cluster,
    override val secondCandidate: Cluster
) : MergingCandidate {

    override fun merge(evaluator: TaxonDistanceEvaluator, mergeMetric: MergeMetric): Cluster {
        val result: Cluster =
            SimpleCluster(firstCandidate.taxonList.plus(secondCandidate.taxonList))

        val pairs = arrayListOf<Pair<Node, Node>>()

        for (firstNode in firstCandidate.taxonList) {
            for (secondNode in secondCandidate.taxonList) {
                val currentDistance = evaluator.evaluate(firstNode.taxon, secondNode.taxon)
                if (distance == currentDistance) {
                    pairs.add(Pair(firstNode, secondNode))
                }
            }
        }

        val bridges = mutableSetOf<IMergingBridge>()
        for ((first, second) in pairs) {
            val firstPossiblePaths = first.getAdjacentTaxonList()
            val secondPossiblePaths = second.getAdjacentTaxonList()


            var bridge: IMergingBridge = emptyMergingBridge()
            for (firstPath in firstPossiblePaths) {
                for (secondPath in secondPossiblePaths) {
                    for ((i, firstNode) in firstPath.reversed().withIndex()) {
                        for ((j, secondNode) in secondPath.reversed().withIndex()) {
                            val bridgeLength = distance - i - j
                            if (bridgeLength < 1)
                                continue

                            val metric = mergeMetric.compute(firstNode, secondNode, bridgeLength)
                            if (metric == Int.MIN_VALUE)
                                continue
                            if (metric > bridge.metric) {
                                bridge = MergingBridge(firstNode, secondNode, bridgeLength, metric)
                            }
                        }
                    }
                }
            }

            bridges.add(bridge)
        }

        bridges.forEach { it.build() }
        return result
    }
}