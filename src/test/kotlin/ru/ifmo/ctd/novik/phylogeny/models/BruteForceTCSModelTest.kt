package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel
import ru.ifmo.ctd.novik.phylogeny.utils.create
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData
import java.io.File

/**
 * @author Dmitry Novik ITMO University
 */
internal class BruteForceTCSModelTest : AbstractTestWithFile() {
    override val testDirectory: String = "samples"
    val phylogeneticModel: PhylogeneticModel = PhylogeneticModel.BRUTE_FORCE_TCS

    override fun runTest(testCaseFile: File) {
        val model = phylogeneticModel.create()
        model.evaluateSimpleData(testCaseFile.absolutePath)
    }
}