package ru.ifmo.ctd.novik.phylogeny.tree.merging

import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.utils.createEdge

/**
 * @author Novik Dmitry ITMO University
 */
open class MergingBridge(
    private val firstNode: Node,
    private val secondNode: Node,
    private val length: Int,
    override val metric: Int
) : IMergingBridge {

    override fun build(nodeList: MutableList<Node>) {
        var current = firstNode

        for (i in 0 until length - 1) {
            val newNode = Node()
            nodeList.add(newNode)
            createEdge(current, newNode)
            current = newNode
        }

        createEdge(current, secondNode)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MergingBridge)
            return false
        return this.firstNode == other.firstNode &&
                this.secondNode == other.secondNode &&
                this.length == other.length &&
                this.metric == other.metric
    }

    override fun hashCode(): Int {
        return 4116 +
                96339 * firstNode.hashCode() +
                26019 * secondNode.hashCode() +
                19372 * length.hashCode() +
                94288 * metric.hashCode()
    }
}