package ru.ifmo.ctd.novik.phylogeny

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import ru.ifmo.ctd.novik.phylogeny.io.input.FastaInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.io.output.PrettyPrinter
import ru.ifmo.ctd.novik.phylogeny.utils.*
import java.io.File

/**
 * @author Novik Dmitry ITMO University
 */
fun main(args: Array<String>) {
    val parser = ArgParser("VPR_MCMC")
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
    val taxonList = reader.readFile(input).distinctBy { it.genome.primary }
    println(taxonList.size)

    val startTime = System.currentTimeMillis()
    when (output) {
        "topology" -> {
            val topology = model.computeTopology(taxonList)
            topology.topology.cluster.terminals.forEach {
                topology.getOrCreateNode(it)
            }
            println(topology.toGraphviz(PrettyPrinter()))
            val distanceMatrix = topology.topology.cluster.distanceMatrix
            File("distances.txt").writeText(distanceMatrix.print())
            println("Distance Matrix is written to distances.txt")
            println(topology.toNewick())
        }
        "phylogeny" -> {
            val phylogeny = model.computePhylogeny(taxonList)
            phylogeny.unify()
//            phylogeny.cluster.label()
            println(phylogeny.cluster.toGraphviz(PrettyPrinter()))
            File("distances.txt").writeText(phylogeny.cluster.distanceMatrix.print())
        }
        else -> error("Unknown computed output format")
    }
    val endTime = System.currentTimeMillis()
    println("Total time: ${(endTime - startTime) / 1_000} sec")
}