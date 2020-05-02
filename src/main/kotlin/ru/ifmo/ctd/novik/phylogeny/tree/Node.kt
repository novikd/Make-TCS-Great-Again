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

    val parent: Node? by lazy {
        neighbors.find { node -> node.next.contains(this) }
    }

    constructor() : this(createTaxon())

    fun connect(node: Node) {
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

    val pathsToAdjacentRealTaxon: List<MutableList<Node>>
        get() {
            fun dfs(node: Node, prev: Node? = null): MutableList<MutableList<Node>> {
                val result = mutableListOf<MutableList<Node>>()
                if (prev != null && node.genome.size == 1) {
                    result.add(mutableListOf())
                    return result
                }

                for (neighbor in node.neighbors) {
                    if (neighbor == prev)
                        continue
                    result.addAll(dfs(neighbor, node))
                }

                result.forEach { x -> x.add(node) }
                return result
            }

            val result = dfs(this)
            if (result.isEmpty())
                result.add(mutableListOf(this))
            result.forEach { it.reverse() }
            return result
        }

    override fun toString(): String {
        return nodeName
    }
}

inline fun Node.bfs(shouldVisit: (Node) -> Boolean, action: (Node, Node) -> Unit) {
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
