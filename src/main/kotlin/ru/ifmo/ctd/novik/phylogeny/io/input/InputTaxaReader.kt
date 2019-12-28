package ru.ifmo.ctd.novik.phylogeny.io.input

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

/**
 * @author Dmitry ITMO University
 */
interface InputTaxaReader {
    fun readFile(filename: String): List<Taxon>
}