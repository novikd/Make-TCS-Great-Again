package ru.ifmo.ctd.novik.phylogeny.io.output

import ru.ifmo.ctd.novik.phylogeny.common.Taxon

class NexusOutputTaxonVisitor : OutputTaxonVisitor {
    override fun visit(taxonList: List<Taxon>): String {
        return buildString {
            appendln("#NEXUS")

            appendln("begin data;")
            appendln("dimensions ntax=${taxonList.size} nchar=${taxonList.first().genome.primary.length};")
            appendln("format datatype=dna gap=-;")
            appendln("matrix")
            appendln(taxonList.joinToString(separator = "\n") { "${it.name} ${it.genome.primary}" })
            appendln(";")
            appendln("end;")

            appendln("begin mrbayes;")
            appendln("set autoclose=yes nowarn=yes;")
            appendln("mcmc nruns=1 ngen=10000 samplefreq=10;")
            appendln("sumt;")
            appendln("end;")
        }
    }
}

fun List<Taxon>.toNexus(): String = NexusOutputTaxonVisitor().visit(this)