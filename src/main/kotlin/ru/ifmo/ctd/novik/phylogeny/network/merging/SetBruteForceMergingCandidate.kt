package ru.ifmo.ctd.novik.phylogeny.network.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistance
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.Branch
import ru.ifmo.ctd.novik.phylogeny.network.Node
import ru.ifmo.ctd.novik.phylogeny.network.metric.MergeMetric
import ru.ifmo.ctd.novik.phylogeny.utils.computeDistinctPositions
import ru.ifmo.ctd.novik.phylogeny.utils.emptyMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.utils.genome
import ru.ifmo.ctd.novik.phylogeny.utils.logger
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

        val (firstGenomes, secondGenomes) = minimum.distance.genomes.unzip()
        updateGenomes(minimum.first, firstGenomes)
        updateGenomes(minimum.second, secondGenomes)

        specify(minimum.first)
        specify(minimum.second)

        val newNodesGenomes = Array<MutableSet<String>>(minimum.distance.value) { mutableSetOf() }
        newNodesGenomes[0].addAll(firstGenomes)

        minimum.distance.forEach { (firstGenome, secondGenome) ->
            val positions = computeDistinctPositions(firstGenome, secondGenome)

            for (i in 1 until minimum.distance.value) {
                val current = newNodesGenomes[i]
                newNodesGenomes[i - 1].forEach { genome ->
                    positions.forEach { position ->
                        if (genome[position] != secondGenome[position]) {
                            val builder = StringBuilder(genome)
                            builder[position] = secondGenome[position]
                            current.add(builder.toString())
                        }
                    }
                }
            }
        }

        val newNodes = mutableListOf<Node>()
        MergingBridge(minimum.first, minimum.second, minimum.distance.value, 0).build(newNodes)
        newNodes.forEachIndexed { index, node -> (node.taxon.genome as MutableGenome).addAll(newNodesGenomes[index + 1]) }
        nodes.addAll(newNodes)

        log.info { "Created ${newNodesGenomes.map { it.size }.sum()} new genomes" }

        if (newNodes.isEmpty()) {
            newNodes.add(minimum.first)
            newNodes.add(minimum.second)
        }

        val mergeEndTime = System.currentTimeMillis()
        log.info { "Clusters merged in ${(mergeEndTime - mergeStartTime) / 1_000.0} seconds" }

        return MergingResult(SimpleCluster(nodes), Branch(newNodes))
    }

    private fun updateGenomes(node: Node, genomes: List<String>) = (node.genome as? MutableGenome)?.replace(genomes)

    override fun compareTo(other: MergingCandidate): Int {
        if (other == emptyMergingCandidate())
            return 1
        if (other is SimpleMergingCandidate)
            return 1
        if (other !is SetBruteForceMergingCandidate)
            throw MergingException("Cannot compare candidates: $this and $other")
        return other.distance.value.compareTo(distance.value)
    }

    private fun specify(start: Node) {
        val queue = ArrayDeque<Node>()
        val visited: MutableSet<Node> = mutableSetOf()
        queue.add(start)
        visited.add(start)

        while (queue.isNotEmpty()) {
            val node = queue.pop()

            node.neighbors.forEach { neighbor ->
                if (neighbor !in visited) {
                    if (neighbor.genome.size > 1) {
                        update(node, neighbor)
                        visited.add(neighbor)
                        queue.add(neighbor)
                    }
                } else {
                    // TODO: update with respect to already visited node
                }
            }
        }
    }

    private fun update(parent: Node, child: Node) {
        val mutableGenome = child.genome as MutableGenome
        mutableGenome.removeIf {
            parent.genome.none { parentGenome -> hammingDistance(this, parentGenome) == 1 }
        }
    }

    override fun toString(): String = "SET_BRUTE_FORCE_MERGING_CANDIDATE { Cluster distance: ${distance.value} }"
}