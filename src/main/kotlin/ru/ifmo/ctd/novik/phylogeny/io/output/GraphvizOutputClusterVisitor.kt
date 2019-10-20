package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
class GraphvizOutputClusterVisitor : OutputClusterVisitor {

    override fun visit(cluster: Cluster): String {
        val node = cluster.taxonList.first()
        return visit(node).joinToString(separator = "\n", prefix = "graph G {\n", postfix = "\n}")
    }

    private fun visit(node: Node): List<String> {
        val result = mutableListOf<String>()
        val visited = mutableSetOf(node)
        for (neighbor in node.neighbors) {
            result.add(printEdge(node, neighbor))
            if (neighbor !in visited) {
                result.addAll(visit(neighbor, visited))
            }
        }
        return result
    }

    private fun visit(node: Node, visited: MutableSet<Node>): List<String> {
        val result = mutableListOf<String>()
        visited.add(node)

        for (neighbor in node.neighbors) {
            if (neighbor !in visited) {
                result.add(printEdge(node, neighbor))
                result.addAll(visit(neighbor, visited))
            }
        }
        return result
    }

    private fun printEdge(node: Node, neighbor: Node) = "\"$node\" -- \"$neighbor\""
}