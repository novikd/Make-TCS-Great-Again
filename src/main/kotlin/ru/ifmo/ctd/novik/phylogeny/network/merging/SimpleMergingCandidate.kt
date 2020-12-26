package ru.ifmo.ctd.novik.phylogeny.network.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistance
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.network.Branch
import ru.ifmo.ctd.novik.phylogeny.network.Node
import ru.ifmo.ctd.novik.phylogeny.network.metric.MergeMetric
import ru.ifmo.ctd.novik.phylogeny.network.metric.TCSMergeMetric
import ru.ifmo.ctd.novik.phylogeny.utils.*

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

    companion object {
        val log = logger()
    }

    override fun merge(): MergingResult {
        val pairs = distance.minimumPoints

        val (first, second) = pairs.random(GlobalExecutionSettings.RANDOM)

        val firstNodes = first.adjacentIntermediateNodes
        val secondNodes = second.adjacentIntermediateNodes

        log.info { "First nodes: ${firstNodes.size} Second nodes: ${secondNodes.size}" }

        var bridge: IMergingBridge = emptyMergingBridge()

        for ((firstNode, i) in firstNodes) {
            for ((secondNode, j) in secondNodes) {
                val bridgeLength = distance.value - i - j
                if (bridgeLength < 1)
                    continue

                val metaData = MergingMetaData(listOf(firstNode), listOf(secondNode), bridgeLength)
                val metric = mergeMetric.compute(firstNode, secondNode, metaData)
                if (metric == Int.MIN_VALUE)
                    continue
                if (metric > bridge.metric) {
                    bridge = MergingBridge(firstNode, secondNode, bridgeLength, metric)
                }
            }
        }

        val newNodes = mutableListOf<Node>()
        newNodes.addAll(firstCluster)
        newNodes.addAll(secondCluster)

        val bridgeNodes = mutableListOf<Node>()
        bridge.build(bridgeNodes)
        newNodes.addAll(bridgeNodes)

        if (bridgeNodes.isEmpty()) {
            val bridge = bridge as MergingBridge
            bridgeNodes.add(bridge.firstNode)
            bridgeNodes.add(bridge.secondNode)
        }

        return MergingResult(SimpleCluster(newNodes), Branch(bridgeNodes))
    }

    override operator fun compareTo(other: MergingCandidate): Int {
        if (other == emptyMergingCandidate())
            return 1
        if (other is SetBruteForceMergingCandidate)
            return -1
        if (other !is SimpleMergingCandidate)
            throw MergingException("Can't compare $this and $other")

        return other.distance.value.compareTo(distance.value)
    }

    override fun toString(): String = "SIMPLE_MERGING_CANDIDATE {Cluster distance: ${distance.value}}"
}