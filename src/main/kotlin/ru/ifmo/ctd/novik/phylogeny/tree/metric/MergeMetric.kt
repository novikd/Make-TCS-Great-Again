package ru.ifmo.ctd.novik.phylogeny.tree.metric

import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingMetaData

/**
 * @author Novik Dmitry ITMO University
 */
interface MergeMetric {
    fun compute(firstNode: Node, secondNode: Node, metaData: MergingMetaData): Int
}