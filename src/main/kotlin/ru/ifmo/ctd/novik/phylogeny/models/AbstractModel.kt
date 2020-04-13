package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.Phylogeny
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.tree.Branch
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingResult

/**
 * @author Novik Dmitry ITMO University
 */
abstract class AbstractModel : IModel {
    abstract val defaultMergingCandidate: MergingCandidate

    protected abstract fun createClusters(taxonList: List<Taxon>): MutableList<Cluster>
    protected abstract fun createMergingCandidate(first: Cluster, second: Cluster): MergingCandidate
    protected abstract fun mergeClusters(mergingCandidate: MergingCandidate): MergingResult

    override fun computeTree(taxonList: List<Taxon>): Phylogeny {
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
            val (newCluster, branch) = mergeClusters(currentCandidate)

            clusters.remove(currentCandidate.firstCluster)
            clusters.remove(currentCandidate.secondCluster)
            clusters.add(newCluster)
            branches.add(branch)
        }
        return Phylogeny(clusters.first(), branches)
    }
}