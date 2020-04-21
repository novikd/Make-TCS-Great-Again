package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistance
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Branch
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric
import ru.ifmo.ctd.novik.phylogeny.tree.metric.TCSMergeMetric
import ru.ifmo.ctd.novik.phylogeny.utils.emptyMergingBridge
import ru.ifmo.ctd.novik.phylogeny.utils.emptyMergingCandidate

/**
 * @author Dmitry Novik ITMO University
 */
open class SimpleMergingCandidate(
        override val firstCluster: Cluster,
        override val secondCluster: Cluster,
        override val taxonDistanceEvaluator: TaxonDistanceEvaluator,
        val distance: ClusterDistance
) : MergingCandidate {

    override val mergeMetric: MergeMetric by lazy { TCSMergeMetric(distance.value, taxonDistanceEvaluator) }

    override fun merge(): MergingResult {
        val pairs = distance.minimumPoints

        val bridges = mutableSetOf<IMergingBridge>()
        for ((first, second) in pairs) {
            val firstPossiblePaths = first.pathsToAdjacentRealTaxon
            val secondPossiblePaths = second.pathsToAdjacentRealTaxon

            var bridge: IMergingBridge = emptyMergingBridge()
            for (firstPath in firstPossiblePaths) {
                for (secondPath in secondPossiblePaths) {
                    for ((i, firstNode) in firstPath.withIndex()) {
                        for ((j, secondNode) in secondPath.withIndex()) {
                            val bridgeLength = distance.value - i - j
                            if (bridgeLength < 1)
                                continue

                            val metaData = MergingMetaData(firstPath.subList(0, i + 1), secondPath.subList(0, j + 1),
                                    bridgeLength)
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
        val newNodes = mutableListOf<Node>()
        newNodes.addAll(firstCluster)
        newNodes.addAll(secondCluster)

        val bridgeNodes = mutableListOf<Node>()
        bridges.first().build(bridgeNodes)
        newNodes.addAll(bridgeNodes)

        if (bridgeNodes.isEmpty()) {
            val bridge = bridges.first() as MergingBridge
            bridgeNodes.add(bridge.firstNode)
            bridgeNodes.add(bridge.secondNode)
        }

        return MergingResult(SimpleCluster(newNodes), Branch(bridgeNodes))
    }

    override operator fun compareTo(other: MergingCandidate): Int {
        if (other == emptyMergingCandidate())
            return 1
        if (other !is SimpleMergingCandidate)
            throw MergingException("Can't compare $this and $other")

        return other.distance.value.compareTo(distance.value)
    }

    override fun toString(): String = "SIMPLE_MERGING_CANDIDATE {Cluster distance: ${distance.value}}"
}