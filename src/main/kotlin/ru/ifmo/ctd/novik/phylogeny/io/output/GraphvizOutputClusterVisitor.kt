package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.Topology

/**
 * @author Novik Dmitry ITMO University
 */
class GraphvizOutputClusterVisitor : OutputClusterVisitor {

    override fun visit(cluster: Cluster): String {
        val node = cluster.nodes.first()
        return visit(node).joinToString(separator = "\n", prefix = "graph G {\n", postfix = "\n}")
    }

    override fun visit(topology: Topology): String = topology.edges
            .joinToString(separator = "\n", prefix = "graph G {\n", postfix = "\n}") { (edge, _) -> edge.toString() }

    private fun visit(node: Node): List<String> {
        val result = mutableListOf<String>()
        val visited = mutableMapOf<Node, Int>()

        visited[node] = 1
        for (neighbor in node.neighbors) {
            if (neighbor !in visited) {
                result.addAll(visit(neighbor, node, visited))
            }
        }
        visited[node] = 2

        return result
    }

    private fun visit(node: Node, prev: Node, visited: MutableMap<Node, Int>): List<String> {
        val result = mutableListOf(printEdge(prev, node))
        if (node in visited)
            return result

        visited[node] = 1

        for (neighbor in node.neighbors) {
            val status = visited.getOrDefault(neighbor, 0)
            if (status == 2 || neighbor == prev)
                continue

            result.addAll(visit(neighbor, node, visited))
        }

        visited[node] = 2
        return result
    }

    private fun printEdge(node: Node, neighbor: Node) = "\"$node\" -- \"$neighbor\""
}