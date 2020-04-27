package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.tree.*
import ru.ifmo.ctd.novik.phylogeny.utils.*

private const val HOTSPOT_DISTANCE_THRESHOLD = 0.1

class HotspotMoveModification(val hotspots: MutableList<Int>) : Modification {

    companion object {
        val log = logger()
    }

    override fun invoke(topology: RootedTopology): RootedTopology {
        val hotspotPosition = (0 until hotspots.size).random(GlobalRandom)
        val hotspot = hotspots[hotspotPosition]

        log.info { "Looking for recombination at $hotspot site" }

        val computedGroups = topology.recombinationGroups.any { it.hotspot == hotspot }
        if (!computedGroups)
            computeRecombinationGroups(topology, hotspot)

        val groups = topology.recombinationGroups.filter { it.hotspot == hotspot }
        if (groups.isNotEmpty()) {
            val group = groups.random(GlobalRandom)
            applyRecombination(topology, group)
        }
        return topology
    }

    private fun applyRecombination(topology: RootedTopology, group: RecombinationGroup) {
        if (group.isUsed) return
        group.setUsed()

        val recombination = group.elements.random(GlobalRandom)

        log.info { "Applying recombination at ${recombination.pos} site: $recombination" }

        val firstParent = topology.getOrCreateNode(recombination.firstParent)
        val secondParent = topology.getOrCreateNode(recombination.secondParent)
        val child = topology.getOrCreateNode(recombination.child)

        if (!removeExParents(topology, child)) {
            log.info { "Abort recombination application" }
            rollbackSplit(topology, firstParent)
            rollbackSplit(topology, secondParent)
            rollbackSplit(topology, child)
            return
        }

        val firstDifference = computeDifference(child.genome, firstParent.genome)
        val secondDifference = computeDifference(child.genome, secondParent.genome)
        val commonDifference = firstDifference intersect secondDifference

        val topologyNode: TopologyNode = if (commonDifference.isNotEmpty()) {
            createAncestor(topology, commonDifference, child)
        } else {
            child
        }

        val fromFirstParent = Edge.create(firstParent, topologyNode)
        firstParent.add(fromFirstParent, directed = true)
        val fromSecondParent = Edge.create(secondParent, topologyNode)
        secondParent.add(fromSecondParent, directed = true)

        createEdge(firstParent.node, topologyNode.node)
        createEdge(secondParent.node, topologyNode.node)

        val toFirstParent = Edge.create(topologyNode, firstParent)
        topologyNode.add(toFirstParent)
        val toSecondParent = Edge.create(topologyNode, secondParent)
        topologyNode.add(toSecondParent)

        topology.topology.add(Pair(fromFirstParent, toFirstParent))
        topology.topology.add(Pair(fromSecondParent, toSecondParent))

        if (child !== topologyNode) {
            if (child.edges.size == 2) {
                mergeTwoEdges(topology, child)
            }
        }
    }

    private fun rollbackSplit(topology: RootedTopology, node: TopologyNode) {
        if (node.edges.size == 2)
            mergeTwoEdges(topology, node)
    }

    private fun mergeTwoEdges(topology: RootedTopology, child: TopologyNode) {
        if (child.next.isEmpty())
            return
        val outEdge = child.next.first()
        val inEdge = child.edges.first { it.end !== outEdge.end }

        val start = inEdge.end
        val end = outEdge.end

        start.removeIf { it.end === child }
        end.removeIf { it.end === child }
        topology.topology.nodes.remove(child)

        topology.topology.remove(inEdge)
        topology.topology.remove(outEdge)

        val path = inEdge.nodes.reversed() + outEdge.nodes.drop(1)
        val newEdge = Edge(start, end, path)
        start.add(newEdge, directed = true)
        val revNewEdge = newEdge.reversed()
        end.add(revNewEdge)
        topology.topology.add(Pair(newEdge, revNewEdge))
    }

    private fun removeExParents(topology: RootedTopology, child: TopologyNode): Boolean {
        val childExParents = child.edges.filter { edge ->
            edge.end.next.any { it.end === child }
        }
        if (childExParents.size != 1)
            return false

        val edgeToParent = childExParents.first()
        val parent = edgeToParent.end
        if (parent === topology.root)
            return false

        child.remove(edgeToParent)
        topology.topology.remove(edgeToParent)

        parent.removeIf { it.end === child }

        val nextNode = edgeToParent.nodes[1]
        child.node.neighbors.removeIf { it === nextNode }
        nextNode.neighbors.removeIf { it === child.node }

        var newTailNode: Node? = null
        for (i in 1 until edgeToParent.nodes.lastIndex) {
            val node = edgeToParent.nodes[i]
            if (node.isRealTaxon) {
                newTailNode = node
                break
            }

            for (group in topology.recombinationGroups) {
                group.elements.removeIf {
                    it.firstParent === node || it.secondParent === node || it.child === node
                }
                if (group.elements.isEmpty())
                    topology.recombinationGroups.remove(group)
            }
            node.neighbors.forEach { it.neighbors.remove(node) }
            topology.topology.cluster.nodes.remove(node)
        }

        if (newTailNode != null) {
            val topologyNode = TopologyNode(newTailNode)
            topology.topology.nodes.add(topologyNode)

            val index = edgeToParent.nodes.indexOf(newTailNode)
            val newPath = edgeToParent.nodes.subList(index, edgeToParent.nodes.size)
            val newEdge = Edge(topologyNode, edgeToParent.end, newPath)
            val revNewEdge = newEdge.reversed()
            topology.topology.add(Pair(newEdge, revNewEdge))

            topologyNode.add(newEdge)
            parent.add(revNewEdge, directed = true)
        } else {
            if (parent.edges.size == 2) {
                mergeTwoEdges(topology, parent)
            }
        }
        return true
    }

    private fun createAncestor(topology: RootedTopology, commonDifference: Set<Pair<Int, Char>>, child: TopologyNode): TopologyNode {
        val builder = StringBuilder(child.genome.primary)
        var current = child.node
        val path = mutableListOf(current)

        for ((index, char) in commonDifference) {
            builder[index] = char
            val newNode = Node()
            (newNode.genome as MutableGenome).add(builder.toString())

            topology.topology.cluster.nodes.add(newNode)
            path.add(newNode)
            createEdge(current, newNode)
            current = newNode
        }
        val ancestor = TopologyNode(current)
        topology.topology.nodes.add(ancestor)

        val fromAncestor = Edge(ancestor, child, path.reversed())
        ancestor.add(fromAncestor, directed = true)
        val toAncestor = Edge(child, ancestor, path)
        child.add(toAncestor)
        topology.topology.add(Pair(fromAncestor, toAncestor))

        return ancestor
    }

    private fun computeRecombinationGroups(topology: RootedTopology, hotspot: Int) {
        val genomes = topology.genomes
        val length = genomes.first().second.length

        val threshold = HOTSPOT_DISTANCE_THRESHOLD * length

        val prefixes = Array(genomes.size) { mutableSetOf<Int>() }
        val suffixes = Array(genomes.size) { mutableSetOf<Int>() }

        for (i in genomes.indices) {
            val iPrefix = genomes[i].second.subSequence(0, hotspot)
            val iSuffix = genomes[i].second.subSequence(hotspot, length)
            for (j in i + 1 until genomes.size) {
                val jPrefix = genomes[j].second.subSequence(0, hotspot)
                val jSuffix = genomes[j].second.subSequence(hotspot, length)

                val prefixDistance = hammingDistance(iPrefix, jPrefix)
                val suffixDistance = hammingDistance(iSuffix, jSuffix)
                if (prefixDistance < threshold && suffixDistance > threshold) {
                    prefixes[i].add(j)
                    prefixes[j].add(i)
                } else if (suffixDistance < threshold && prefixDistance > threshold) {
                    suffixes[i].add(j)
                    suffixes[j].add(i)
                }
            }
        }

        for (i in genomes.indices) {
            val iPrefixes = prefixes[i]
            val iSuffixes = suffixes[i]

            if (iPrefixes.isNotEmpty() && iSuffixes.isNotEmpty()) {
                val prefix = iPrefixes.random(GlobalRandom)
                val suffix = iSuffixes.random(GlobalRandom)

                topology.add(Recombination(genomes[prefix].first, genomes[suffix].first, genomes[i].first, hotspot))
            }
        }

        log.info {
            "Recombination groups at $hotspot site: ${topology.recombinationGroups.count { it.hotspot == hotspot }}\nTotal number of recombination groups: ${topology.recombinationGroups.size}"
        }
    }
}