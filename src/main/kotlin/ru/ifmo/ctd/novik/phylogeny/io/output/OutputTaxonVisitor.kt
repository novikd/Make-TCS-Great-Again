package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

interface OutputTaxonVisitor {
    fun visit(taxonList: List<Taxon>): String
}