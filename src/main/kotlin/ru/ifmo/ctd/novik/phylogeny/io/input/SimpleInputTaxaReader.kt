package ru.ifmo.ctd.novik.phylogeny.io.input

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome
import java.io.File

/**
 * @author Novik Dmitry ITMO University
 */
class SimpleInputTaxaReader : InputTaxaReader {
    override fun readFile(filename: String): List<Taxon> {
        val result = mutableListOf<Taxon>()
        var id = 0
        File(filename).bufferedReader().forEachLine {
            result.add(Taxon(id, name = "taxon${id++}", genome = it.toGenome()))
        }
        return result
    }
}