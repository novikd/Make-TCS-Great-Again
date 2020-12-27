package ru.ifmo.ctd.novik.phylogeny.io.input

import ru.ifmo.ctd.novik.phylogeny.common.ObservedTaxon
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome
import java.io.File

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleInputTaxaReader : InputTaxaReader {
    override fun readFile(filename: String): List<ObservedTaxon> {
        val result = mutableListOf<ObservedTaxon>()
        var id = 0
        File(filename).bufferedReader().forEachLine {
            result.add(ObservedTaxon(id, name = "taxon${id++}", genome = it.toGenome()))
        }
        return result
    }
}