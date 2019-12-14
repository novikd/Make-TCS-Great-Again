package ru.ifmo.ctd.novik.phylogeny.io.output

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.createEdge

internal class GraphvizOutputClusterVisitorTest {

    @Test
    fun `graphviz cycle`() {
        val expected = "graph G {\n" +
                "\"intermediate0 \" -- \"intermediate1 \"\n" +
                "\"intermediate1 \" -- \"intermediate2 \"\n" +
                "\"intermediate2 \" -- \"intermediate0 \"\n" +
                "}"
        val node1 = Node()
        val node2 = Node()
        val node3 = Node()
        createEdge(node1, node2)
        createEdge(node1, node3)
        createEdge(node2, node3)

        val cluster = SimpleCluster(listOf(node1, node2, node3))
        val actual = GraphvizOutputClusterVisitor().visit(cluster)
        assertEquals(expected, actual)
    }
}