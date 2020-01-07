package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric
import ru.ifmo.ctd.novik.phylogeny.tree.metric.TCSMergeMetric
import ru.ifmo.ctd.novik.phylogeny.utils.emptyMergingBridge

/**
 * @author Dmitry Novik ITMO University
 */
open class SimpleMergingCandidate(
    override val firstCandidate: Cluster,
    override val secondCandidate: Cluster,
    override val taxonDistanceEvaluator: TaxonDistanceEvaluator,
    val distance: Int
) : MergingCandidate {

    override val mergeMetric: MergeMetric by lazy { TCSMergeMetric(distance, taxonDistanceEvaluator) }

    override fun merge(): Cluster {
        val result: Cluster =
            SimpleCluster(firstCandidate.taxonList.plus(secondCandidate.taxonList))

        val pairs = findEdgeNodes(taxonDistanceEvaluator)

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

                            val metaData = MergingMetaData(first, i, second, j, bridgeLength)
                            val metric = mergeMetric.compute(firstNode, secondNode, metaData)
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

    private fun findEdgeNodes(evaluator: TaxonDistanceEvaluator): ArrayList<Pair<Node, Node>> {
        val pairs = arrayListOf<Pair<Node, Node>>()

        for (firstNode in firstCandidate.taxonList) {
            for (secondNode in secondCandidate.taxonList) {
                val currentDistance = evaluator.evaluate(firstNode.taxon, secondNode.taxon)
                if (distance == currentDistance) {
                    pairs.add(Pair(firstNode, secondNode))
                }
            }
        }
        return pairs
    }
}