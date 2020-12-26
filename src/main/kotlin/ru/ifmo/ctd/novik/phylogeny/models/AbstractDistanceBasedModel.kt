package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.Phylogeny
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.network.Branch
import ru.ifmo.ctd.novik.phylogeny.network.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.network.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.network.merging.MergingResult
import ru.ifmo.ctd.novik.phylogeny.utils.logger
import ru.ifmo.ctd.novik.phylogeny.utils.toRooted
import ru.ifmo.ctd.novik.phylogeny.utils.topology

/**
 * @author Novik Dmitry ITMO University
 */
abstract class AbstractDistanceBasedModel : IModel {
    abstract val defaultMergingCandidate: MergingCandidate

    companion object {
        val log = logger()
    }

    protected abstract fun createClusters(taxonList: List<Taxon>): MutableList<Cluster>
    protected abstract fun createMergingCandidate(first: Cluster, second: Cluster, genomNumber: Int): MergingCandidate
    protected abstract fun mergeClusters(mergingCandidate: MergingCandidate): MergingResult

    override fun computePhylogeny(taxonList: List<Taxon>): Phylogeny {
        val clusters = createClusters(taxonList)

        val branches = mutableListOf<Branch>()
        while (clusters.size > 1) {
            var currentCandidate: MergingCandidate = defaultMergingCandidate

            val genomNumber = clusters.map { it.genomeNumber }.sum()
            for (i in clusters.indices) {
                val firstCluster = clusters[i]
                for (j in i + 1 until clusters.size) {
                    val secondCluster = clusters[j]
                    val newCandidate = createMergingCandidate(firstCluster, secondCluster, genomNumber)
                    if (newCandidate > currentCandidate)
                        currentCandidate = newCandidate
                }
            }
            log.info { "Merging candidate: $currentCandidate" }
            val (newCluster, branch) = mergeClusters(currentCandidate)

            clusters.remove(currentCandidate.firstCluster)
            clusters.remove(currentCandidate.secondCluster)
            clusters.add(newCluster)
            branches.add(branch)

            log.info {
                "Current total number of genomes: ${clusters.map { it.genomeNumber }.sum()}\nSeparate sizes: ${clusters.map { it.genomeNumber }.joinToString(separator = " ")}"
            }
            log.info {
                "Current nodes number: ${clusters.map { it.nodes.size }.joinToString(separator = " ")}"
            }
        }
        return Phylogeny(clusters.first(), branches)
    }

    override fun computeTopology(taxonList: List<Taxon>): RootedTopology {
        val (cluster, branches) = computePhylogeny(taxonList)
        val topology = cluster.topology()
        val root = branches.last().nodes.random()
        return topology.toRooted(root)
    }
}