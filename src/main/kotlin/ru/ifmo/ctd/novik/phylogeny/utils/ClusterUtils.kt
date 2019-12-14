package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.tree.Node

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

    return visit(this.taxonList.first())
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