package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Cluster

/**
 * @author Novik Dmitry ITMO University
 */
interface OutputClusterVisitor {
    fun visit(cluster: Cluster): String
}