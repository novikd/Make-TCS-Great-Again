package ru.ifmo.ctd.novik.phylogeny.io.input

import ru.ifmo.ctd.novik.phylogeny.common.ObservedTaxon
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome
import java.io.File

/**
 * @author Dmitry Novik ITMO University
 */
class FastaInputTaxaReader : InputTaxaReader {
    override fun readFile(filename: String): List<ObservedTaxon> {
        val reader = File(filename).bufferedReader()
        val result = mutableListOf<ObservedTaxon>()

        var name = ""
        var genome = ""
        var id = 0
        reader.forEachLine {
            if (it.startsWith(">")) {
                if (genome.isNotEmpty())
                    result.add(ObservedTaxon(id++, name, genome.toGenome()))
                name = it.removePrefix(">")
                genome = ""
            } else {
                genome += it
            }
        }

        if (result.size == id && genome.isNotEmpty())
            result.add(ObservedTaxon(id++, name, genome.toGenome()))
        return result
    }
}