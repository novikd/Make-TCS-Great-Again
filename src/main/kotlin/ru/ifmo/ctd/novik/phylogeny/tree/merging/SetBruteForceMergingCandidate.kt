package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistance
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Branch
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric
import ru.ifmo.ctd.novik.phylogeny.utils.*
import java.util.*

/**
 * @author Dmitry Novik ITMO University
 */
class SetBruteForceMergingCandidate(
        override val firstCluster: Cluster,
        override val secondCluster: Cluster,
        private val distance: ClusterDistance
) : MergingCandidate {
    override val taxonDistanceEvaluator: TaxonDistanceEvaluator
        get() = TODO("not implemented")
    override val mergeMetric: MergeMetric
        get() = TODO("not implemented")

    companion object {
        val log = logger()
    }

    override fun merge(): MergingResult {
        val mergeStartTime = System.currentTimeMillis()
        val nodes = mutableListOf<Node>()
        nodes.addAll(firstCluster)
        nodes.addAll(secondCluster)

        val minimum = distance.minimumPoints.first()
        val firstGraphDistances = minimum.first.computeGraphDistancesToAllTerminals()
        val secondGraphDistances = minimum.second.computeGraphDistancesToAllTerminals()

        val firstGenome = minimum.first.genome
        if (firstGenome is MutableGenome) {
            firstGenome.removeIf { minimum.distance.genomes.none { (genome, _) -> this == genome } }
        }
        val secondGenome = minimum.second.genome
        if (secondGenome is MutableGenome) {
            secondGenome.removeIf { minimum.distance.genomes.none { (_, genome) -> this == genome } }
        }

        specify(minimum.first, firstGraphDistances)
        specify(minimum.second, secondGraphDistances)

        val newNodesGenomes = Array<MutableSet<String>>(minimum.distance.value - 1) { mutableSetOf() }
        minimum.distance.forEach { (firstGenome, secondGenome) ->
            val positions = computeDistinctPositions(firstGenome, secondGenome)
            permutations(distance.value).map {
                val builder = StringBuilder(firstGenome)
                it.map { index ->
                    builder[positions[index]] = secondGenome[positions[index]]
                    builder.toString()
                }
            }.forEach { genomes ->
                newNodesGenomes.forEachIndexed { index, set -> set.add(genomes[index]) }
            }
        }

        val newNodes = mutableListOf<Node>()
        MergingBridge(minimum.first, minimum.second, minimum.distance.value, 0).build(newNodes)
        newNodes.forEachIndexed { index, node -> (node.taxon.genome as MutableGenome).addAll(newNodesGenomes[index]) }
        nodes.addAll(newNodes)

        if (newNodes.isEmpty()) {
            newNodes.add(minimum.first)
            newNodes.add(minimum.second)
        }

        val mergeEndTime = System.currentTimeMillis()
        log.info { "Clusters merged in ${(mergeEndTime - mergeStartTime) / 1_000.0} seconds" }

        return MergingResult(SimpleCluster(nodes), Branch(newNodes))
    }

    override fun compareTo(other: MergingCandidate): Int {
        if (other == emptyMergingCandidate())
            return 1
        if (other !is SetBruteForceMergingCandidate)
            throw MergingException("Cannot compare candidates: $this and $other")
        return other.distance.value.compareTo(distance.value)
    }

    private fun specify(node: Node, graphDistances: Map<Node, Int>) {
        graphDistances.forEach { (graphNode, distance) ->
            if (graphNode == node)
                return
            val genome = graphNode.genome as MutableGenome
            genome.removeIf {
                node.genome.none { currentGenome -> hammingDistance(this, currentGenome) == distance }
            }
        }
    }

    override fun toString(): String = "SET_BRUTE_FORCE_MERGING_CANDIDATE { Cluster distance: ${distance.value} }"
}