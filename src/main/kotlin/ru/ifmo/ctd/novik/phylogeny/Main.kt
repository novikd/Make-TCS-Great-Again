package ru.ifmo.ctd.novik.phylogeny

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import ru.ifmo.ctd.novik.phylogeny.utils.*

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

    parser.parse(args)

    val model = phylogeneticModel.create()
    val phylogeneticTree = model.evaluateSimpleData(input)

    print(phylogeneticTree.toGraphviz())
}