package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.RootedTopology
import ru.ifmo.ctd.novik.phylogeny.utils.GlobalRandom
import ru.ifmo.ctd.novik.phylogeny.utils.logger

class HotspotMoveModification(val hotspots: MutableList<Int>) : Modification {

    companion object {
        val log = logger()
    }

    override fun invoke(topology: RootedTopology): RootedTopology {
        val hotspotPosition = (0 until hotspots.size).random(GlobalRandom)
        val hotspot = hotspots[hotspotPosition]

        log.info { "Looking for recombination at $hotspot site" }

        val genomes = topology.genomes
        val length = genomes.first().second.length

        val prefixes = Array(genomes.size) { mutableSetOf<Int>()}
        val suffixes = Array(genomes.size) { mutableSetOf<Int>()}

        for (i in genomes.indices) {
            val iPrefix = genomes[i].second.subSequence(0, hotspot)
            val iSuffix = genomes[i].second.subSequence(hotspot, length)
            for (j in i + 1 until genomes.size) {
                val jPrefix = genomes[j].second.subSequence(0, hotspot)
                val jSuffix = genomes[j].second.subSequence(hotspot, length)

                val prefixDistance = hammingDistance(iPrefix, jPrefix)
                val suffixDistance = hammingDistance(iSuffix, jSuffix)
                if (prefixDistance == 0 && suffixDistance != 0) {
                    prefixes[i].add(j)
                    prefixes[j].add(i)
                } else if (suffixDistance == 0 && prefixDistance != 0) {
                    suffixes[i].add(j)
                    suffixes[j].add(i)
                }
            }
        }

        for (i in genomes.indices) {
            val iPrefixes = prefixes[i]
            val iSuffixes = suffixes[i]

            if (iPrefixes.isNotEmpty() && iSuffixes.isNotEmpty()) {
                val prefix = iPrefixes.first()
                val suffix = iSuffixes.first()
                log.info {
                    "Found recombination: { ${genomes[prefix].second} + ${genomes[suffix].second} -> ${genomes[i].second} }"
                }

                val firstParent = topology.nodes.first { it.node === genomes[prefix].first }
                val secondParent = topology.nodes.first { it.node === genomes[suffix].first }
                val iNode = genomes[i].first
                val containingEdge = topology.edges.first { edge -> edge.contains(iNode) }
                val topologyNode = when (iNode) {
                    containingEdge.start.node -> containingEdge.start
                    containingEdge.end.node -> containingEdge.end
                    else -> {
                        val (firstPart, _) = containingEdge.split(iNode)
                        firstPart.start.remove(firstPart)
                        firstPart.end.removeIf { this.end === firstPart.start }
                        firstPart.end
                    }
                }

                firstParent.add(Edge.create(firstParent, topologyNode))
                secondParent.add(Edge.create(secondParent, topologyNode))

                topologyNode.add(Edge.create(topologyNode, firstParent))
                topologyNode.add(Edge.create(topologyNode, secondParent))
            }
        }

        return topology
    }
}