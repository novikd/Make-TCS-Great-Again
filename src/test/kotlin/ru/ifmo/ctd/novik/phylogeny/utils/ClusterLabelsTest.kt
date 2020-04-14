package ru.ifmo.ctd.novik.phylogeny.utils

import org.junit.jupiter.api.Test
import ru.ifmo.ctd.novik.phylogeny.base.AbstractTestWithFile

internal class ClusterLabelsTest : AbstractTestWithFile() {
    override val testDirectory: String = "samples"

    @Test
    fun `undirected label computation`() {
        runTests {
            val model = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
            val phylogeny = model.evaluateSimpleData(this.absolutePath)
            phylogeny.cluster.label().toGraphviz()
        }
    }

    @Test
    fun `directed label computation`() {
        runTests {
            val model = PhylogeneticModel.SET_BRUTE_FORCE_TCS.create()
            val phylogeny = model.evaluateSimpleData(this.absolutePath)
            phylogeny.directed().label().toGraphviz()
        }
    }
}