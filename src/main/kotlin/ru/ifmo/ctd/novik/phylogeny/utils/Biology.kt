package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*

/**
 * @author Dmitry Novik ITMO University
 */

fun String.toGenome(): Genome = Genome(this)

fun String.toMutableGenome(): MutableGenome = MutableGenome(this)

internal var taxonGenerator = generateSequence (Taxon(0)) { Taxon(it.id + 1) }.iterator()

fun createTaxon(): Taxon = taxonGenerator.next()
