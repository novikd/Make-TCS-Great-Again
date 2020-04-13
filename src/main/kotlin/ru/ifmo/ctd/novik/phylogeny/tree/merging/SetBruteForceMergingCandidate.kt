package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistance
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
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

    override fun merge(): Cluster {
        val nodes = mutableListOf<Node>()
        nodes.addAll(firstCluster)
        nodes.addAll(secondCluster)

        val minimum = distance.minimumPoints.first()
        val firstGraphDistances = minimum.first.computeAllGraphDistances()
        val secondGraphDistances = minimum.second.computeAllGraphDistances()
        specify(minimum.first, secondGraphDistances)
        specify(minimum.second, firstGraphDistances)

        val newNodesGenomes = Array<MutableSet<String>>(minimum.distance.value - 1) { mutableSetOf() }
        minimum.first.taxon.genome.forEach { firstGenome ->
            minimum.second.taxon.genome.forEach { secondGenome ->
                val positions = computeDistinctPositions(firstGenome, secondGenome)
                permutations(distance.value).map {
                    val builder = StringBuilder(firstGenome)
                    it.map { index ->
                        builder[positions[index]] = secondGenome[positions[index]]
                        builder.toString()
                    }
                }.forEach { genomes ->
                    val good = genomes.mapIndexed { index, genome ->
                        isCorrect(genome, firstGraphDistances, index + 1)
                        && isCorrect(genome, secondGraphDistances, distance.value - index - 1)
                    }.all { it }
                    if (good)
                        newNodesGenomes.forEachIndexed { index, set -> set.add(genomes[index]) }
                }
            }
        }

        val newNodes = mutableListOf<Node>()
        MergingBridge(minimum.first, minimum.second, minimum.distance.value, 0).build(newNodes)
        newNodes.forEachIndexed { index, node -> (node.taxon.genome as MutableGenome).addAll(newNodesGenomes[index]) }
        nodes.addAll(newNodes)
        return SimpleCluster(nodes)
    }

    override fun compareTo(other: MergingCandidate): Int {
        if (other == emptyMergingCandidate())
            return 1
        if (other !is SetBruteForceMergingCandidate)
            throw MergingException("Cannot compare candidates: $this and $other")
        return other.distance.value.compareTo(distance.value)
    }

    private fun specify(node: Node, graphDistances: Map<Node, Int>) {
        val queue = ArrayDeque<Pair<Node, Int>>()
        val visited = mutableSetOf<Node>()
        queue.add(node to 0)
        visited.add(node)

        while (queue.isNotEmpty()) {
            val (current, distanceOffset) = queue.pop()
            val genome = current.taxon.genome
            if (genome is MutableGenome) {
                genome.filter { currentGenome ->
                    !isCorrect(currentGenome, graphDistances, distance.value + distanceOffset)
                }.forEach {
                    genome.remove(it)
                }
            }
            for (neighbor in current.neighbors) {
                if (visited.contains(neighbor)) continue
                queue.add(neighbor to distanceOffset + 1)
                visited.add(neighbor)
            }
        }
    }

    private fun isCorrect(currentGenome: String, graphDistances: Map<Node, Int>, distanceOffset: Int): Boolean {
        return graphDistances.all { (otherNode, graphDistance) ->
            otherNode.taxon.genome.isEmpty || otherNode.taxon.genome.any { otherGenome ->
                hammingDistance(currentGenome, otherGenome) == graphDistance + distanceOffset
            }
        }
    }
}