package ru.ifmo.ctd.novik.phylogeny.mcmc.modifications

import kotlinx.coroutines.*
import ru.ifmo.ctd.novik.phylogeny.common.MutableGenome
import ru.ifmo.ctd.novik.phylogeny.common.SNP
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.events.Recombination
import ru.ifmo.ctd.novik.phylogeny.events.RecombinationGroup
import ru.ifmo.ctd.novik.phylogeny.events.RecombinationGroupAmbassador
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.network.*
import ru.ifmo.ctd.novik.phylogeny.utils.*
import kotlin.math.min

const val HOTSPOT_DISTANCE_THRESHOLD = 0.01

class HotspotMoveModification(val hotspots: MutableList<Int>) : Modification {

    companion object {
        val log = logger()
    }

    override fun invoke(topology: RootedTopology): RootedTopology {
        val hotspot = hotspots.random(GlobalExecutionSettings.RANDOM)

        log.info { "Looking for recombination at $hotspot site" }

        val recombinationGroups = computeRecombinationGroups(topology, hotspot).filter { !it.isUsed }

        if (recombinationGroups.isNotEmpty()) {
            val group = recombinationGroups.random(GlobalExecutionSettings.RANDOM)
            applyRecombination(topology, group)
        }
        return topology
    }

    private fun applyRecombination(topology: RootedTopology, group: RecombinationGroup) {
        debug {
            if (group.isUsed) error("Applying recombination for used group")
        }
        val recombination = group.elements.random(GlobalExecutionSettings.RANDOM)

        log.info { "Applying recombination at ${recombination.pos} site: $recombination" }

        val firstParent = topology.getOrCreateNode(recombination.firstParent)
        val secondParent = topology.getOrCreateNode(recombination.secondParent)
        val child = topology.getOrCreateNode(recombination.child)

        val createdEdges = mutableListOf<Edge>()
        val deletedPath = mutableListOf<Node>()
        if (!isConsistentRecombination(child, firstParent, secondParent, topology)
                || !removeExParents(topology, child, firstParent, secondParent, createdEdges, deletedPath)) {
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

        createdEdges.add(fromFirstParent)
        createdEdges.add(fromSecondParent)

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
                topology.mergeTwoEdges(child)
            }
        }

        debug {
            val errorNode = topology.topology.cluster.find { node -> !topology.edges.any { it.contains(node) } }
            if (errorNode != null)
                error("Node $errorNode removal was incorrect")
            topology.checkInvariant()
        }

        val ambassador = RecombinationGroupAmbassador(recombination, topologyNode, createdEdges, deletedPath)
        group.setUsed(ambassador)
        topology.add(ambassador)
    }

    private fun isConsistentRecombination(
            child: TopologyNode,
            firstParent: TopologyNode,
            secondParent: TopologyNode,
            topology: RootedTopology
    ): Boolean {
        if (!child.next.all { it.end !== firstParent && it.end !== secondParent })
            return false
        val childExParents = child.edges.filter { edge ->
            edge.end.next.any { it.end === child }
        }

        if (childExParents.size != 1)
            return false
        val edgeToParent = childExParents.first()
        val stopNode = edgeToParent.nodes.subList(1, edgeToParent.nodes.size).firstOrNull { it.isRealTaxon }
        if (stopNode == null) {
            val parent = edgeToParent.end
            if (parent === firstParent || parent === secondParent)
                return false
            if (parent.edges.size == 3 && parent.next.size == 2) {
                val parentNeighbor = parent.edges.map { it.end }
                if (parent.edges.any { it.end.edges.any { it.end in parentNeighbor } }) {
                    return false
                }
            }
            if (topology.recombinationEdges.any { it.start === parent || it.end === parent })
                return false
        }

        return true
    }

    private fun rollbackSplit(topology: RootedTopology, node: TopologyNode) {
        if (node.edges.size == 2)
            topology.mergeTwoEdges(node)
    }

    private fun removeExParents(
            topology: RootedTopology,
            child: TopologyNode,
            firstParent: TopologyNode,
            secondParent: TopologyNode,
            createdEdges: MutableList<Edge>,
            deletedPath: MutableList<Node>
    ): Boolean {
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

        deletedPath.add(child.node)
        var newTailNode: Node? = null
        for (i in 1 until edgeToParent.nodes.lastIndex) {
            val node = edgeToParent.nodes[i]
            deletedPath.add(node)
            if (node.isRealTaxon) {
                newTailNode = node
                break
            }

            debug {
                log.info { "Deleting node: ${node.nodeName}" }
            }

            node.neighbors.forEach { it.neighbors.remove(node) }
            node.neighbors.clear()
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
            createdEdges.add(revNewEdge)
        } else {
            deletedPath.add(parent.node)
            if (parent != firstParent && parent != secondParent)
                topology.mergeTwoEdges(parent)
        }

        debug {
            for (i in 1 until deletedPath.lastIndex) {
                if (deletedPath[i].neighbors.size != 0)
                    error("Deleted node ${deletedPath[i]} has neighbors")
            }
        }

        return true
    }

    private fun createAncestor(topology: RootedTopology, commonDifference: Set<Pair<Int, Char>>, child: TopologyNode): TopologyNode {
        var current = child.node
        val path = mutableListOf(current)

        for ((index, char) in commonDifference) {
            val newNode = Node(current.genome.mutate(listOf(SNP(index, char))))

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

    private fun computeRecombinationGroups(topology: RootedTopology, hotspot: Int): List<RecombinationGroup> {
        val genomes = topology.genomes
        val length = genomes.first().second.length

        val threshold = HOTSPOT_DISTANCE_THRESHOLD * length

        val prefixes = Array(genomes.size) { mutableSetOf<Int>() }
        val suffixes = Array(genomes.size) { mutableSetOf<Int>() }

        val distances = Array (genomes.size) { IntArray(genomes.size) }

        fun computeDistances(iRange: IntRange, jRange: IntRange) {
            for (i in iRange) {
                val iPrefix = genomes[i].second.subSequence(0, hotspot)
                val iSuffix = genomes[i].second.subSequence(hotspot, length)
                for (j in jRange) {
                    val jPrefix = genomes[j].second.subSequence(0, hotspot)
                    val jSuffix = genomes[j].second.subSequence(hotspot, length)

                    val prefixDistance = hammingDistance(iPrefix, jPrefix)
                    val suffixDistance = hammingDistance(iSuffix, jSuffix)
                    distances[i][j] = prefixDistance + suffixDistance
                    distances[j][i] = prefixDistance + suffixDistance
                    if (prefixDistance < threshold && suffixDistance > threshold) {
                        prefixes[i].add(j)
                        prefixes[j].add(i)
                    } else if (suffixDistance < threshold && prefixDistance > threshold) {
                        suffixes[i].add(j)
                        suffixes[j].add(i)
                    }
                }
            }
        }

        runBlocking {
            val jobs = mutableListOf<Job>()
            for (i in 0 until (genomes.size + 9) / 10) {
                for (j in i until (genomes.size + 9) / 10) {
                    jobs.add(this.launch {
                        computeDistances(
                                10 * i until min(10 * (i + 1), genomes.size),
                                10 * j until min(10 * (j + 1), genomes.size))
                    })
                }
            }
            jobs.forEach { it.join() }
        }

        val groupToChildren = mutableListOf<Pair<RecombinationGroup, MutableSet<Int>>>()
        for (i in genomes.indices) {
            val iPrefixes = prefixes[i]
            val iSuffixes = suffixes[i]

            if (iPrefixes.isNotEmpty() && iSuffixes.isNotEmpty()) {
                val (group, children) = getOrCreateRecombinationGroup(i, hotspot, groupToChildren, distances, threshold)
                children.add(i)

                val prefixGroups = groupGenomes(iPrefixes, distances)
                val suffixGroups = groupGenomes(iSuffixes, distances)

                prefixGroups.forEach { prefix ->
                    suffixGroups.forEach { suffix ->
                        val prefixIndex = prefix.genomes.random(GlobalExecutionSettings.RANDOM)
                        val suffixIndex = suffix.genomes.random(GlobalExecutionSettings.RANDOM)
                        val recombination = Recombination(
                                genomes[prefixIndex].first,
                                genomes[suffixIndex].first,
                                genomes[i].first, hotspot, i)
                        group.add(recombination)
                    }
                }
            }
        }
        val recombinationGroups = groupToChildren.map { it.first }.filterNot { it.elements.isEmpty() }

        val filteredAmbassadors = topology.recombinationAmbassadors.filter { it.recombination.pos == hotspot }
        runBlocking {
            val jobs = mutableListOf<Job>()
            groupToChildren.forEach { (group, children) ->
                jobs.add(this.launch {
                    val groupAmbassador = filteredAmbassadors.firstOrNull { ambassador ->
                        children.any { hammingDistance(genomes[it].second, ambassador.recombination.child.genome.primary) < 10 }
                    }
                    if (groupAmbassador != null)
                        group.setUsed(groupAmbassador)
                })
            }
            jobs.forEach { it.join() }
        }

        log.info {
            "Recombination groups at $hotspot site: ${recombinationGroups.size}"
        }
        return recombinationGroups
    }

    private class GenomeGroup(val genomes: MutableList<Int>)

    private fun groupGenomes(genomeSet: MutableSet<Int>, distances: Array<IntArray>): MutableList<GenomeGroup> {
        val genomes = genomeSet.toList()
        val genomeGroups = mutableListOf<GenomeGroup>()

        fun getOrCreateGenomeGroup(index: Int): GenomeGroup {
            val group = genomeGroups.firstOrNull {
                it.genomes.any { distances[it][index] < 10 }
            }
            if (group != null)
                return group
            genomeGroups.add(GenomeGroup(mutableListOf()))
            return genomeGroups.last()
        }

        for (i in genomes.indices) {
            val genomeGroup = getOrCreateGenomeGroup(genomes[i])
            genomeGroup.genomes.add(genomes[i])
        }
        return genomeGroups
    }

    private fun getOrCreateRecombinationGroup(
        childIndex: Int,
        hotspot: Int,
        groupToChildren: MutableList<Pair<RecombinationGroup, MutableSet<Int>>>,
        distances: Array<IntArray>,
        threshold: Double
    ): Pair<RecombinationGroup, MutableSet<Int>> {
        val group = tryGetRecombinationGroup(childIndex, hotspot, groupToChildren, distances, threshold)
        if (group != null) {
            return group
        }

        val newGroup = RecombinationGroup(hotspot)
        val newChildrenSet = mutableSetOf<Int>()
        val result = Pair(newGroup, newChildrenSet)
        groupToChildren.add(result)
        return result
    }

    private fun tryGetRecombinationGroup(
        childIndex: Int,
        hotspot: Int,
        groupToChildren: MutableList<Pair<RecombinationGroup, MutableSet<Int>>>,
        distances: Array<IntArray>,
        threshold: Double
    ): Pair<RecombinationGroup, MutableSet<Int>>? {
        return runBlocking {
            val subtasks = mutableListOf<Deferred<Pair<Boolean, Pair<RecombinationGroup, MutableSet<Int>>>>>()
            for ((group, children) in groupToChildren)
            {
                subtasks.add(
                        this.async {
                            val iter = children.iterator()
                            while (isActive && iter.hasNext()) {
                                val index = iter.next()
                                if (distances[childIndex][index] <= threshold)
                                    return@async Pair(true, Pair(group, children))
                            }
                            return@async Pair(false, Pair(group, children))
                        })
            }
            val result = subtasks.map { it.await() }.filter { it.first }

            if (result.size == 1)
                return@runBlocking result.first().second
            if (result.isNotEmpty()) {
                val oldSize = groupToChildren.size
                val newElements = mutableListOf<Recombination>()
                val newChildren = mutableSetOf<Int>()
                result.forEach {
                    newElements.addAll(it.second.first.elements)
                    newChildren.addAll(it.second.second)
                    groupToChildren.removeIf { pair -> pair.first === it.second.first }
                }
                val newGroup = RecombinationGroup(hotspot, newElements)
                val resultPair = Pair(newGroup, newChildren)
                groupToChildren.add(resultPair)
                val newSIze = groupToChildren.size
                log.info { "Old size: {$oldSize} New size: {$newSIze}" }
                return@runBlocking resultPair
            }


//            val count = result.filter { it.first }.count()
//            if (count != 0)
//                log.info { "Can merge $count" }

            return@runBlocking null
//            val index = subtasks.indexOfFirst { it.await().first }
//            if (index != -1) {
//                for (i in index + 1..subtasks.lastIndex)
//                    subtasks[i].cancel()
//                return@runBlocking subtasks[index].await().second
//            }
//            return@runBlocking null
        }
    }
}