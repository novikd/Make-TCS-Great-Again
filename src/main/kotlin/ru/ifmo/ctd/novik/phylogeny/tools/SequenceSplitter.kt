package ru.ifmo.ctd.novik.phylogeny.tools

import ru.ifmo.ctd.novik.phylogeny.common.ConstantGenome
import ru.ifmo.ctd.novik.phylogeny.common.ObservedTaxon
import ru.ifmo.ctd.novik.phylogeny.common.Taxon
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.utils.ConfigurationDelegate
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * @author Dmitry Novik ITMO University
 */

fun List<Taxon>.toNexus(): String {
    return buildString {
        appendLine("#NEXUS")

        appendLine("begin data;")
        appendLine("dimensions ntax=${this@toNexus.size} nchar=${this@toNexus.first().genome.primary.length};")
        appendLine("format datatype=dna gap=-;")
        appendLine("matrix")
        appendLine(this@toNexus.joinToString(separator = "\n") { "${it.name} ${it.genome.primary}" })
        appendLine(";")
        appendLine("end;")

        appendLine("begin mrbayes;")
        appendLine("set autoclose=yes nowarn=yes;")
        appendLine("mcmc nruns=1 ngen=10000 samplefreq=10;")
        appendLine("sumt;")
        appendLine("end;")
    }
}

fun main(args: Array<String>) {
    var taxonList = FastaInputTaxaReader().readFile(args.last()).map { Pair(it.name, it.genome.primary) }

    val hotspots by ConfigurationDelegate()
    val sortedHotspots = hotspots.sorted()

    var count = 0
    val resultSubSequences = mutableListOf<List<Pair<String, String>>>()
    for (hotspot in sortedHotspots) {
        val i = hotspot - count
        val prefix = mutableListOf<Pair<String, String>>()
        val suffix = mutableListOf<Pair<String, String>>()

        for ((name, seq) in taxonList) {
            prefix.add(Pair(name, seq.substring(0, i)))
            suffix.add(Pair(name, seq.substring(i)))
        }
        resultSubSequences.add(prefix)
        taxonList = suffix
        count += i
    }
    resultSubSequences.add(taxonList)

    var iter = 0
    ZipOutputStream(File("sequences.zip").outputStream()).use { zipOut ->
        resultSubSequences.forEach { subSequences ->
            val groupedSubSequences = subSequences.groupBy { it.second }
            val unique = groupedSubSequences.filter { it.value.size == 1 }.flatMap { it.value }
            val multiple = groupedSubSequences.filter { it.value.size != 1 }.map { it.value }.toList()
            val indices = IntArray(multiple.size) { 0 }

            if (multiple.isEmpty()) {
                val subTaxonList = unique.mapIndexed { index, (name, seq) ->
                    ObservedTaxon(index, name, ConstantGenome(seq))
                }
                zipOut.putNextEntry(ZipEntry("sequences${iter++}.nex"))
                zipOut.write(subTaxonList.toNexus().toByteArray())
            } else {
                while (multiple[0].size != indices[0]) {
                    val subTaxonList = unique.mapIndexed { index, (name, seq) ->
                        ObservedTaxon(index, name, ConstantGenome(seq))
                    } + multiple.mapIndexed { index, list ->
                        list[indices[index]]
                    }.mapIndexed { index, (name, seq) ->
                        ObservedTaxon(unique.size + index, name, ConstantGenome(seq))
                    }

                    zipOut.putNextEntry(ZipEntry("sequences$iter.nex"))
                    zipOut.write(subTaxonList.toNexus().toByteArray())

                    for (i in indices.lastIndex downTo 0) {
                        if (++indices[i] != multiple[i].size || i == 0)
                            break
                        indices[i] = 0
                    }
                    println("Passed ${++iter} iterations")
                }
            }
        }
    }
//    val result = buildString {
//        resultSubSequences.forEach { subSequences ->
//            val groupedSubSequences = subSequences.groupBy { it.second }
//            val unique = groupedSubSequences.filter { it.value.size == 1 }.flatMap { it.value }
//            val multiple = groupedSubSequences.filter { it.value.size != 1 }.map { it.value }.toList()
//            val indices = IntArray(multiple.size) { 0 }
//
//            var iter = 0
//            while (multiple[0].size != indices[0]) {
//                val model = PhylogeneticModel.BASE_TCS.create()
//                val subTaxonList = unique.mapIndexed { index, (name, seq) ->
//                    Taxon(index, name, Genome(seq))
//                } + multiple.mapIndexed { index, list ->
//                    list[indices[index]]
//                }.mapIndexed { index, (name, seq) ->
//                    Taxon(unique.size + index, name, Genome(seq))
//                }
//
//
//                ZipEntry("sequences$iter.nex")
//                subTaxonList.toNexus()
//
//
//                val topology = model.computeTopology(subTaxonList)
//                this.appendln(topology.toNewick())
//
//                for (i in indices.lastIndex downTo 0) {
//                    if (++indices[i] != multiple[i].size || i == 0)
//                        break
//                    indices[i] = 0
//                }
//                println("Passed ${++iter} iterations")
//            }
//        }
//    }

//    File("split.nexus").writeText(result)
}