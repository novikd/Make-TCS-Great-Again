package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.SimpleCluster
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingException
import ru.ifmo.ctd.novik.phylogeny.tree.merging.SimpleMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.emptyMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.metric.TCSMergeMetric

/**
 * @author Novik Dmitry ITMO University
 */
class TCSModel(
    private val distanceEvaluator: ClusterDistanceEvaluator
) : ClusterDistanceEvaluator by distanceEvaluator, AbstractModel() {

    override val defaultMergingCandidate: MergingCandidate = emptyMergingCandidate()

    override fun createClusters(taxonList: List<Taxon>): MutableList<Cluster> {
        taxonDistanceEvaluator.preprocessTaxonList(taxonList)

        return taxonList.map { x -> SimpleCluster(x) }.toMutableList()
    }

    override fun createMergingCandidate(first: Cluster, second: Cluster): MergingCandidate {
        val distance = evaluate(first, second)
        return SimpleMergingCandidate(first, second, distance)
    }

    override fun mergeClusters(mergingCandidate: MergingCandidate): Cluster {
        if (mergingCandidate !is SimpleMergingCandidate)
            throw MergingException("SimpleMergingCandidate expected, but $mergingCandidate found")
        return mergingCandidate.merge(
                taxonDistanceEvaluator,
                TCSMergeMetric(mergingCandidate.distance, taxonDistanceEvaluator)
        )
    }
}