package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.tree.Node

class PrettyPrinter : Printer {
    override fun print(node: Node): String = node.nodeName
}