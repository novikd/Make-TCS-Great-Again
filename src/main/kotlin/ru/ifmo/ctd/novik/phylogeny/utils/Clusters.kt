package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import java.util.*

fun Cluster.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)

fun Cluster.traverse(action: (Node.() -> Unit)) {
    val visited = mutableSetOf<Node>()
    fun visit(node: Node) {
        visited.add(node)
        action(node)
        node.neighbors.forEach {
            if (it !in visited)
                visit(it)
        }
    }

    return visit(this.nodes.first())
}

fun Cluster.unify(): Cluster {
    var counter = 0
    traverse {
        if (!this.isRealTaxon) {
            this.nodeName = "intermediate${++counter}"
        }
    }
    return this
}

typealias Path = MutableList<Node>

fun createEdge(v: Node, u: Node, directed: Boolean = false) {
    v.connect(u)
    if (!directed)
        u.connect(v)
}



fun Node.computeAllGraphDistances(): Map<Node, Int> {
    val result = hashMapOf<Node, Int>()
    result[this] = 0

    val queue: Queue<Node> = ArrayDeque()
    queue.add(this)

    while (queue.isNotEmpty()) {
        val currentNode = queue.poll()
        val currentDistance = result[currentNode]!!
        for (neighbor in currentNode.neighbors) {

            if (!result.contains(neighbor)) {
                result[neighbor] = currentDistance + 1
                queue.add(neighbor)
            }
        }
    }

    return result
}

fun Node.computeGraphDistances(): Map<Node, Int> = this.computeAllGraphDistances().filterKeys { x -> x.isRealTaxon }
