package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.RootedPhylogeny
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.tree.Topology

/**
 * @author Novik Dmitry ITMO University
 */
interface OutputClusterVisitor {
    fun visit(cluster: Cluster): String
    fun visit(topology: Topology): String
    fun visit(phylogeny: RootedPhylogeny): String
    fun visit(topology: RootedTopology): String
}