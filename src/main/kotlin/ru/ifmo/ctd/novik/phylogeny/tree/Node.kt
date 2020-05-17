package ru.ifmo.ctd.novik.phylogeny.tree

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.utils.createTaxon
import ru.ifmo.ctd.novik.phylogeny.utils.genome
import java.util.*

/**
 * @author Novik Dmitry ITMO University
 */
data class Node(val taxon: Taxon) {
    val isRealTaxon = taxon.genome.isReal
    var nodeName: String = taxon.name
    val neighbors: MutableList<Node> = mutableListOf()
    val next: MutableList<Node> = mutableListOf()

    constructor() : this(createTaxon())

    fun connect(node: Node) {
        if (neighbors.contains(node))
            error("Already existed neighbor")
        neighbors.add(node)
    }

    val adjacentIntermediateNodes: Map<Node, Int>
        get() {
            val result = mutableMapOf(this to 0)

            bfs({ it !in result && it.genome.size != 1 }) {
                prev, node -> result[node] = result[prev]!! + 1
            }
            return result
        }

    override fun toString(): String {
        return nodeName
    }
}

inline fun Node.bfs(shouldVisit: (Node) -> Boolean, action: (prev: Node, node: Node) -> Unit) {
    val queue = ArrayDeque<Node>()
    queue.add(this)

    while (queue.isNotEmpty()) {
        val node = queue.pop()
        node.neighbors.forEach { neighbor ->
            if (shouldVisit(neighbor)) {
                action(node, neighbor)
                queue.add(neighbor)
            }
        }
    }
}
