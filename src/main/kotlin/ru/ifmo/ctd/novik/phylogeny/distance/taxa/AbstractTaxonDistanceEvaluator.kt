package ru.ifmo.ctd.novik.phylogeny.distance.taxa

import ru.ifmo.ctd.novik.phylogeny.common.Genome
import ru.ifmo.ctd.novik.phylogeny.common.GenomeOption
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance

/**
 * @author Dmitry Novik ITMO University
 */
abstract class AbstractTaxonDistanceEvaluator : TaxonDistanceEvaluator {
    protected abstract fun evaluate(lhs: Genome, rhs: Genome): TaxonDistance

    protected fun evaluate(lhs: GenomeOption, rhs: GenomeOption): Int = hammingDistance(lhs, rhs)

    override fun evaluate(lhs: Taxon, rhs: Taxon): TaxonDistance = evaluate(lhs.genome, rhs.genome)
}