package ru.ifmo.ctd.novik.phylogeny.io.input

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import java.io.File

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleInputTaxaReader {
    fun readFile(filename: String): List<Taxon> {
        val result = mutableListOf<Taxon>()
        var id = 0
        File(filename).bufferedReader().forEachLine {
            result.add(Taxon(id, name = "taxon${id++}", genome = it))
        }
        return result
    }
}