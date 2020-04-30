package ru.ifmo.ctd.novik.phylogeny

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.utils.*
import java.io.File

/**
 * @author Novik Dmitry ITMO University
 */
fun main(args: Array<String>) {
    val parser = ArgParser("Make-TCS-Great-Again")
    val phylogeneticModel by parser.option(
            ModelChoice,
            shortName = "m",
            fullName = "model",
            description = "Model to run").default(PhylogeneticModel.BASE_TCS)
    val input by parser.option(ArgType.String, shortName = "i", description = "Input file").required()
    val output by parser.option(ArgType.String, shortName = "c", description = "Computed output").default("phylogeny")

    parser.parse(args)

    val hotspots by ConfigurationDelegate()

    val model = phylogeneticModel.create(hotspots)

    val reader = when (File(input).extension) {
        "fas" -> FastaInputTaxaReader()
        else -> SimpleInputTaxaReader()
    }
    val taxonList = reader.readFile(input).distinctBy { it.genome.primary }.take(25)
    println(taxonList.size)

    val startTime = System.currentTimeMillis()
    when (output) {
        "topology" -> {
            val topology = model.computeTopology(taxonList)
            println(topology.toGraphviz())
        }
        "phylogeny" -> {
            val phylogeny = model.computePhylogeny(taxonList)
            phylogeny.unify()
            println(phylogeny.cluster.toGraphviz())
        }
        else -> error("Unknown computed output format")
    }
    val endTime = System.currentTimeMillis()
    println("Total time: ${(endTime - startTime) / 1_000} sec")
}