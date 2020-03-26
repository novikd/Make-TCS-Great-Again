package ru.ifmo.ctd.novik.phylogeny.models

import ru.ifmo.ctd.novik.phylogeny.utils.PhylogeneticModel

/**
 * @author Dmitry Novik ITMO University
 */
internal class SetBruteForceTCSModelTest : AbstractModelTest() {
    override val testDirectory: String = "samples"
    override val outputExtensionPrefix: String = ".setBf"
    override val phylogeneticModel: PhylogeneticModel = PhylogeneticModel.SET_BRUTE_FORCE_TCS
}