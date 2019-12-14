package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.Cluster
import ru.ifmo.ctd.novik.phylogeny.io.input.SimpleInputTaxaReader
import ru.ifmo.ctd.novik.phylogeny.models.IModel

fun IModel.evaluateSimpleData(dataFile: String): Cluster {
    val reader = SimpleInputTaxaReader()
    val taxonList = reader.readFile(dataFile)

    return this.computeTree(taxonList).unify()
}