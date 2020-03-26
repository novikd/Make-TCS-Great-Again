package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.distance.cluster.ClusterDistanceEvaluator
import ru.ifmo.ctd.novik.phylogeny.tree.merging.SetBruteForceMergingCandidate
import ru.ifmo.ctd.novik.phylogeny.tree.merging.MergingCandidate

/**
 * @author Dmitry Novik ITMO University
 */
class SetBruteForceTCSModel(distanceEvaluator: ClusterDistanceEvaluator) : TCSModel(distanceEvaluator) {
    override fun createMergingCandidate(first: Cluster, second: Cluster): MergingCandidate {
        return SetBruteForceMergingCandidate(first, second, evaluate(first, second))
    }
}