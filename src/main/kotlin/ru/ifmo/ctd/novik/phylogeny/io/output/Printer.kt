package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.tree.Node

interface Printer {
    fun print(node: Node): String
}