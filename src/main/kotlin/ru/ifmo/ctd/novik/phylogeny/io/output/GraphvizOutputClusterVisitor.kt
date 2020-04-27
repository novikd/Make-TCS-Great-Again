package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.RootedPhylogeny
import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.tree.Topology
import ru.ifmo.ctd.novik.phylogeny.utils.genome

/**
 * @author Novik Dmitry ITMO University
 */
class GraphvizOutputClusterVisitor : OutputClusterVisitor {

    override fun visit(cluster: Cluster): String {
        val node = cluster.nodes.first()
        return visit(node).joinToString(separator = "\n", prefix = "graph G {\n", postfix = "\n}")
    }

    override fun visit(topology: Topology): String = topology.edges
            .joinToString(separator = "\n", prefix = "graph G {\n", postfix = "\n}") { (edge, _) -> printEdge(edge) }

    override fun visit(phylogeny: RootedPhylogeny): String {
        return visitDirected(phylogeny.root).joinToString(separator = "\n", prefix = "digraph G {\n", postfix = "\n}")
    }

    override fun visit(topology: RootedTopology): String {
        return topology.edges.joinToString(separator = "\n", prefix = "digraph G {\n", postfix = "\n}") {
            "\"${it.start.node.nodeName}\" -> \"${it.end.node.nodeName}\""
        }
    }

    private fun visitDirected(node: Node): List<String> {
        val result = mutableListOf<String>()

        node.next.forEach { next ->
            result.add(printEdge(node, next, "->"))
            result.addAll(visitDirected(next))
        }
        return result
    }

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

    private fun printNode(node: Node) = if (node.isRealTaxon) node.genome.toString() else "${node.nodeName}{${node.genome}}"

    private fun printEdge(edge: Edge) = "\"NODE: ${printNode(edge.start.node)}\" -- \"NODE: ${printNode(edge.end.node)}\""

    private fun printEdge(node: Node, neighbor: Node, edgeSequence: String = "--") =
            "\"${printNode(node)}\" $edgeSequence \"${printNode(neighbor)}\""
}