package ru.ifmo.ctd.novik.phylogeny.common

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleCluster(override val nodes: List<Node>) : Cluster {
    override val terminals: List<Node>
        get() = nodes.filter(Node::isRealTaxon)

    constructor(taxon: Taxon) : this(listOf(Node(taxon)))

    override fun clone(): Cluster {
        val generation = mutableMapOf<Node, Node>()
        val newNodes = mutableListOf<Node>()
        nodes.forEach { node ->
            val newNode = node.copy()
            generation[node] = newNode
            newNodes.add(newNode)
        }

        generation.forEach { old, new ->
            new.neighbors.addAll(old.neighbors.map { generation[it]!! })
            new.next.addAll(old.next.map { generation[it]!! })
        }

        return SimpleCluster(newNodes)
    }

    override fun iterator(): Iterator<Node> = nodes.iterator()
}