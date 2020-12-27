package ru.ifmo.ctd.novik.phylogeny.io.input

import ru.ifmo.ctd.novik.phylogeny.common.ObservedTaxon

/**
 * @author Dmitry Novik ITMO University
 */
interface InputTaxaReader {
    fun readFile(filename: String): List<ObservedTaxon>
}