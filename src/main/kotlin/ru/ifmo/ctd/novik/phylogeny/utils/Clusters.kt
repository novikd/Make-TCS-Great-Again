package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.Topology
import ru.ifmo.ctd.novik.phylogeny.tree.TopologyNode
import java.util.*

fun Cluster.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)
fun Topology.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)

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

fun Cluster.topology(): Topology {
    val hubs = mutableMapOf<Node, TopologyNode>()
    val visited = mutableSetOf<Node>()

    val queue = ArrayDeque<Node>()
    queue.add(nodes.firstOrNull { it.neighbors.size != 2 } ?: terminals.first())

    val edges = mutableListOf<Pair<Edge, Edge>>()

    while (queue.isNotEmpty()) {
        val node = queue.pop()
        val topologyNode = hubs.computeIfAbsent(node, ::TopologyNode)

        visited.add(node)
        node.neighbors.filter { !visited.contains(it) }.forEach {
            val path = mutableListOf(node, it)
            var previous = node
            var current = it

            while (current.neighbors.size == 2) {
                val next = current.neighbors.first { it != previous }
                path.add(next)
                visited.add(current)

                if (visited.contains(next))
                    break
                previous = current
                current = next
            }

            val topologyCurrent = hubs.computeIfAbsent(current, ::TopologyNode)
            val edge = Edge(topologyNode, topologyCurrent, path)
            topologyNode.add(edge)
            val revEdge = Edge(topologyCurrent, topologyNode, path.reversed())
            topologyCurrent.add(revEdge)

            edges.add(Pair(edge, revEdge))
            queue.add(current)
        }
    }

    return Topology(hubs.values.toList(), edges)
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
