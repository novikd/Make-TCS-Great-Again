package ru.ifmo.ctd.novik.phylogeny.distance.cluster

import ru.ifmo.ctd.novik.phylogeny.distance.taxa.TaxonDistance
import ru.ifmo.ctd.novik.phylogeny.tree.Node

/**
 * @author Dmitry Novik ITMO University
 */
data class DistanceMinimumPoint(val first: Node, val second: Node, val distance: TaxonDistance)