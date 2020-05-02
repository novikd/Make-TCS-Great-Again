package ru.ifmo.ctd.novik.phylogeny.models

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile
import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel
import ru.ifmo.ctd.novik.phylogeny.utils.create
import ru.ifmo.ctd.novik.phylogeny.utils.evaluateSimpleData

/**
 * @author Dmitry Novik ITMO University
 */
internal class BruteForceTCSModelTest : AbstractTestWithFile() {
    override val testDirectory: String = "samples"
    val phylogeneticModel: PhylogeneticModel = PhylogeneticModel.BRUTE_FORCE_TCS

    @Test
    fun `Brute force doesn't fail`() {
        runTests {
            val model = phylogeneticModel.create()
            model.evaluateSimpleData(this.absolutePath)
        }
    }
}