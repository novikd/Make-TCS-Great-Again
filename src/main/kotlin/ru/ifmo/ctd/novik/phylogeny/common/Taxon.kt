package ru.ifmo.ctd.novik.phylogeny.common

/**
 * @author Novik Dmitry ITMO University
 */
data class Taxon(val id: Int, val name: String = "intermediate${id}", val genome: String = "")

internal var taxonGenerator = generateSequence (Taxon(0)) { Taxon(it.id + 1) }.iterator()

fun createTaxon(): Taxon = taxonGenerator.next()