package ru.ifmo.ctd.novik.phylogeny.io.output

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.utils.createEdge

internal class GraphvizOutputClusterVisitorTest {

    @Test
    fun `graphviz cycle`() {
        val expected = "graph G {\n" +
                "\"intermediate0{unknown genome}\" -- \"intermediate1{unknown genome}\"\n" +
                "\"intermediate1{unknown genome}\" -- \"intermediate2{unknown genome}\"\n" +
                "\"intermediate2{unknown genome}\" -- \"intermediate0{unknown genome}\"\n" +
                "}"
        val node1 = Node()
        val node2 = Node()
        val node3 = Node()
        createEdge(node1, node2)
        createEdge(node1, node3)
        createEdge(node2, node3)

        val cluster = SimpleCluster(mutableListOf(node1, node2, node3))
        val actual = GraphvizOutputClusterVisitor().visit(cluster)
        assertEquals(expected, actual)
    }
}