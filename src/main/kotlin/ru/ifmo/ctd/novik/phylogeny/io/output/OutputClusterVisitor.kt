package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.network.Cluster
import ru.ifmo.ctd.novik.phylogeny.network.RootedPhylogeny
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.network.Topology

/**
 * @author Dmitry Novik ITMO University
 */
interface OutputClusterVisitor {
    fun visit(cluster: Cluster): String
    fun visit(topology: Topology): String
    fun visit(phylogeny: RootedPhylogeny): String
    fun visit(topology: RootedTopology): String
}