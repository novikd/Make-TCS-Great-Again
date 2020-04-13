package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel

internal class TCSModelTest : AbstractModelTest() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".base"
    override val phylogeneticModel: PhylogeneticModel = PhylogeneticModel.BASE_TCS
}