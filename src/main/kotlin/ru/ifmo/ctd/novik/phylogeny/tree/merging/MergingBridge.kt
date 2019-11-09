package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.createEdge

/**
 * @author Novik Dmitry ITMO University
 */
class MergingBridge(
    private val firstNode: Node,
    private val secondNode: Node,
    private val length: Int,
    override val metric: Int
) : IMergingBridge {

    override fun build() {
        var current = firstNode

        for (i in 0 until length - 1) {
            val newNode = Node()
            createEdge(current, newNode)
            current = newNode
        }

        createEdge(current, secondNode)
    }
}