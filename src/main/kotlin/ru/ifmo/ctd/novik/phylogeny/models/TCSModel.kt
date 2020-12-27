package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.network.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.network.merging.MergingResult
import ru.ifmo.ctd.novik.phylogeny.network.merging.SimpleMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.utils.emptyMergingCandidate

/**
 * @author Dmitry Novik ITMO University
 */
open class TCSModel(
    private val distanceEvaluator: ClusterDistanceEvaluator
) : ClusterDistanceEvaluator by distanceEvaluator, AbstractDistanceBasedModel() {

    override val defaultMergingCandidate: MergingCandidate = emptyMergingCandidate()

    override fun createClusters(taxonList: List<Taxon>): MutableList<Cluster> {
        return taxonList.map { x -> SimpleCluster(x) }.toMutableList()
    }

    override fun createMergingCandidate(first: Cluster, second: Cluster, genomNumber: Int): MergingCandidate {
        val distance = evaluate(first, second)
        return SimpleMergingCandidate(first, second, taxonDistanceEvaluator, distance)
    }

    override fun mergeClusters(mergingCandidate: MergingCandidate): MergingResult {
        return mergingCandidate.merge()
    }
}