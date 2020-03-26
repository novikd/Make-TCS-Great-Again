package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.IGenome
import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistance
import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.metric.MergeMetric
import ru.ifmo.ctd.novik.phylogeny.utils.emptyMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome

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
        for (minimum in distance.minimumPoints) {
            specify(minimum.first, minimum.distance.firstGenome.toGenome())
            specify(minimum.second, minimum.distance.secondGenome.toGenome())
            MergingBridge(minimum.first, minimum.second, minimum.distance.value, 0).build(nodes)
        }
        return SimpleCluster(nodes)
    }

    override fun compareTo(other: MergingCandidate): Int {
        if (other == emptyMergingCandidate())
            return 1
        if (other !is SetBruteForceMergingCandidate)
            throw MergingException("Cannot compare candidates: $this and $other")
        return distance.value.compareTo(other.distance.value)
    }

    private fun specify(node: Node, previous: IGenome, visited: MutableSet<Node> = mutableSetOf()) {
        visited.add(node)
        val genome = node.taxon.genome
        if (genome is MutableGenome) {
            genome
                    .filter { current -> !previous.any { it.zip(current).count { (a, b) -> a != b } == 1 } }
                    .forEach { current -> genome.remove(current) }
        }
        for (neighbor in node.neighbors) {
            if (visited.contains(neighbor)) continue // TODO: perform check of already visited nodes
            specify(neighbor, genome, visited)
        }
    }
}