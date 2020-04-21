package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.common.Phylogeny
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology

interface IModel {
    fun computePhylogeny(taxonList: List<Taxon>): Phylogeny
    fun computeTopology(taxonList: List<Taxon>): RootedTopology
}