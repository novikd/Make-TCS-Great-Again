package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader

fun main() {
    val list = FastaInputTaxaReader().readFile("input.fas")

    val d = Array(list.size) { IntArray(list.size) }

    list.forEachIndexed { index1, taxon1 ->
        list.forEachIndexed { index2, taxon2 ->
            d[index1][index2] = hammingDistance(taxon1.genome.primary, taxon2.genome.primary)
        }
    }

    val output = buildString {
        d.map { it.joinToString(separator = " ") }.forEach {
            append(it)
            append("\n")
        }
    }
    print(output)
}