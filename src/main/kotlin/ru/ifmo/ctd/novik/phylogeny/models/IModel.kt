package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.common.Taxon

interface IModel {
    fun computeTree(taxonList: List<Taxon>): Cluster
}