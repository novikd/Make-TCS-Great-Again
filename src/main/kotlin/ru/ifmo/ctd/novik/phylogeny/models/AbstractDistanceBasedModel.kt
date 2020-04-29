package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.Phylogeny
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.tree.Branch
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingResult
import ru.ifmo.ctd.novik.phylogeny.utils.logger

/**
 * @author Novik Dmitry ITMO University
 */
abstract class AbstractDistanceBasedModel : IModel {
    abstract val defaultMergingCandidate: MergingCandidate

    companion object {
        val log = logger()
    }

    protected abstract fun createClusters(taxonList: List<Taxon>): MutableList<Cluster>
    protected abstract fun createMergingCandidate(first: Cluster, second: Cluster): MergingCandidate
    protected abstract fun mergeClusters(mergingCandidate: MergingCandidate): MergingResult

    override fun computePhylogeny(taxonList: List<Taxon>): Phylogeny {
        val clusters = createClusters(taxonList)

        val branches = mutableListOf<Branch>()
        while (clusters.size > 1) {
            var currentCandidate: MergingCandidate = defaultMergingCandidate

            for (i in clusters.indices) {
                val firstCluster = clusters[i]
                for (j in i + 1 until clusters.size) {
                    val secondCluster = clusters[j]
                    val newCandidate = createMergingCandidate(firstCluster, secondCluster)
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
        error("$this can not compute topology")
    }
}