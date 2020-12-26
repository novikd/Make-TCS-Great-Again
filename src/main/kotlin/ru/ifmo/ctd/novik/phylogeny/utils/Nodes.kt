package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.network.Node
import java.util.ArrayDeque

/**
 * @author Dmitry Novik ITMO University
 */
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

val Node.adjacentIntermediateNodes: Map<Node, Int>
    get() {
        val result = mutableMapOf(this to 0)

        bfs({ it !in result && it.genome.size != 1 }) {
                prev, node -> result[node] = result[prev]!! + 1
        }
        return result
    }
