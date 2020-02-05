package ru.ifmo.ctd.novik.phylogeny.io.input

import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.utils.toGenome
import java.io.File

/**
 * @author Dmitry ITMO University
 */
class FastaInputTaxaReader : InputTaxaReader {
    override fun readFile(filename: String): List<Taxon> {
        val reader = File(filename).bufferedReader()
        val result = mutableListOf<Taxon>()

        var name = ""
        var genome = ""
        var id = 0
        reader.forEachLine {
            if (it.startsWith(">")) {
                if (genome.isNotEmpty())
                    result.add(Taxon(id++, name, genome.toGenome()))
                name = it.removePrefix(">")
                genome = ""
            } else {
                genome += it
            }
        }

        if (result.size == id && genome.isNotEmpty())
            result.add(Taxon(id++, name, genome.toGenome()))
        return result
    }
}