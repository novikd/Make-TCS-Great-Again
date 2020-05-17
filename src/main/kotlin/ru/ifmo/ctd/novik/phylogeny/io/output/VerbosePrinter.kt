package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.utils.genome

class VerbosePrinter : Printer {
    override fun print(node: Node): String =
            if (node.isRealTaxon) node.genome.toString() else "${node.nodeName}{${node.genome}}"
}