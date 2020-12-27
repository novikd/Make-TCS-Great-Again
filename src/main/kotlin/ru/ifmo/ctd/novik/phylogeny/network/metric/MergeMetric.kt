package ru.ifmo.ctd.novik.phylogeny.network.metric

import ru.ifmo.ctd.novik.phylogeny.network.Node
import ru.ifmo.ctd.novik.phylogeny.network.merging.MergingMetaData

/**
 * @author Dmitry Novik ITMO University
 */
interface MergeMetric {
    fun compute(firstNode: Node, secondNode: Node, metaData: MergingMetaData): Int
}