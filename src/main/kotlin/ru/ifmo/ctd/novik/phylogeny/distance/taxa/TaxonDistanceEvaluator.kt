package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Novik Dmitry ITMO University
 */
interface TaxonDistanceEvaluator {
    fun evaluate(lhs: Taxon, rhs: Taxon): Int
    fun preprocessTaxonList(taxonList: List<Taxon>)
}