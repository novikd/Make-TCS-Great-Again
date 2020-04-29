package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.utils.genome

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleCluster(override val nodes: MutableList<Node>) : Cluster {
    override val terminals: List<Node>
        get() = nodes.filter(Node::isRealTaxon)

    override val genomeNumber: Int
        get() = nodes.map { it.genome.size }.sum()

    constructor(taxon: Taxon) : this(mutableListOf(Node(taxon)))

    override fun clone(): ClusterCloneResult {
        val generation = mutableMapOf<Node, Node>()
        val newNodes = mutableListOf<Node>()
        nodes.forEach { node ->
            val newNode = node.copy()
            generation[node] = newNode
            newNodes.add(newNode)
        }

        generation.forEach { (old, new) ->
            new.neighbors.addAll(old.neighbors.map { generation[it]!! })
            new.next.addAll(old.next.map { generation[it]!! })
        }

        val invariant = generation.all { (old, new) -> old.neighbors.size == new.neighbors.size && old.next.size == new.next.size }
        if (!invariant)
            error("Cluster clone error")

        return ClusterCloneResult(SimpleCluster(newNodes), generation)
    }

    override fun iterator(): Iterator<Node> = nodes.iterator()
}