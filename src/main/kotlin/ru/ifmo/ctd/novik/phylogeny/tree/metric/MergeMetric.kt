package ru.ifmo.ctd.novik.phylogeny.tree.metric

import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Novik Dmitry ITMO University
 */
interface MergeMetric {
    fun compute(firstNode: Node, secondNode: Node, bridgeLength: Int): Int
}