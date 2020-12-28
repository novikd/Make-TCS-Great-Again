package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.io.output.VerbosePrinter
import java.io.File

internal class UndirectedClusterLabelsTest : AbstractTestWithFile() {
    override val testDirectory: String = "samples"

    override fun runTest(testCaseFile: File) {
        val model = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
        val phylogeny = model.evaluateSimpleData(testCaseFile.absolutePath)
        phylogeny.cluster.label().toGraphviz(VerbosePrinter())
    }
}