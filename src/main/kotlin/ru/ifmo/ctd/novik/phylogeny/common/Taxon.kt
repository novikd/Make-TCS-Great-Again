package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Novik Dmitry ITMO University
 */
data class Taxon(val id: Int, val genome: String)

internal var nextTaxonId = 0

fun createTaxon(): Taxon = Taxon(nextTaxonId, "intermediate${nextTaxonId++} ")