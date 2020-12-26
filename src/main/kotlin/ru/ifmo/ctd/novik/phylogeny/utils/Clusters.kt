package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*
import ru.ifmo.ctd.novik.phylogeny.distance.hammingDistance
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.io.output.NewickOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.io.output.Printer
import ru.ifmo.ctd.novik.phylogeny.settings.GlobalExecutionSettings
import ru.ifmo.ctd.novik.phylogeny.tree.*
import java.util.*

fun Cluster.toGraphviz(printer: Printer): String = GraphvizOutputClusterVisitor(printer).visit(this)
fun Topology.toGraphviz(printer: Printer): String = GraphvizOutputClusterVisitor(printer).visit(this)
fun RootedPhylogeny.toGraphviz(printer: Printer): String = GraphvizOutputClusterVisitor(printer).visit(this)
fun RootedTopology.toGraphviz(printer: Printer): String = GraphvizOutputClusterVisitor(printer).visit(this)

fun RootedTopology.toNewick(): String = NewickOutputClusterVisitor().visit(this)

fun Cluster.traverse(action: (Node.() -> Unit)) {
    val visited = mutableSetOf<Node>()
    fun visit(node: Node) {
        visited.add(node)
        action(node)
        node.neighbors.forEach {
            if (it !in visited)
                visit(it)
        }
    }

    return visit(this.nodes.first())
}

fun Cluster.unify(): Cluster {
    var counter = 0
    traverse {
        if (!this.isRealTaxon) {
            this.nodeName = "intermediate${++counter}"
        }
    }
    return this
}

fun Cluster.topology(): Topology {
    val hubs = mutableMapOf<Node, TopologyNode>()
    val visited = mutableSetOf<Node>()

    val queue = ArrayDeque<Node>()
    queue.add(nodes.firstOrNull { it.neighbors.size != 2 } ?: terminals.first())

    val edges = mutableListOf<Pair<Edge, Edge>>()

    while (queue.isNotEmpty()) {
        val node = queue.pop()
        val topologyNode = hubs.computeIfAbsent(node, ::TopologyNode)

        visited.add(node)
        node.neighbors.filter { !visited.contains(it) }.forEach {
            val path = mutableListOf(node, it)
            var previous = node
            var current = it

            while (current.neighbors.size == 2) {
                val next = current.neighbors.first { it != previous }
                path.add(next)
                visited.add(current)

                if (visited.contains(next))
                    break
                previous = current
                current = next
            }

            val topologyCurrent = hubs.computeIfAbsent(current, ::TopologyNode)
            val edge = Edge(topologyNode, topologyCurrent, path)
            topologyNode.add(edge)
            val revEdge = Edge(topologyCurrent, topologyNode, path.reversed())
            topologyCurrent.add(revEdge)

            edges.add(Pair(edge, revEdge))
            queue.add(current)
        }
    }

    return Topology(this, hubs.values.toMutableList(), edges)
}

fun Cluster.label(): Cluster {
    val labels = mutableMapOf<Node, Array<MutableSet<Char>>>()
    val length = this.terminals.first().taxon.genome.primary.length
    val labeled = nodes.filter { !it.genome.isEmpty && it.neighbors.any { node -> node.genome.isEmpty } }

    val nodesWithOptions = nodes.filter { it.genome.size > 1 }
    for (node in nodesWithOptions) {
        if (node.genome.size == 1)
            continue
        val genome = node.genome as MutableGenome
        genome.replace(listOf(genome.primary))
        node.bfs({ v -> v.genome.size > 1 }) { prev, curr ->
            val currGenome = curr.genome as MutableGenome
            val currOption = currGenome.first { hammingDistance(it, prev.genome.primary) == 1 }
            currGenome.replace(listOf(currOption))
        }
    }

    labeled.forEach { node ->
        val genome = node.genome.primary
        labels[node] = Array(length) { mutableSetOf(genome[it]) }
    }

    val queue = ArrayDeque(labeled)

    val labelingQueue = ArrayDeque<Node>()

    while (queue.isNotEmpty()) {
        val node = queue.pop()

        node.neighbors.filter { it !in labels }.forEach { neighbor ->
            val unprocessed = neighbor.neighbors.filter { it !in labels }.count()
            if (unprocessed < 2) {
                val entry = Array(length) { mutableSetOf<Char>() }
                entry.forEachIndexed { index, currentSet ->
                    val list = neighbor.neighbors.filter { it in labels }.map { node -> labels[node]!![index] }
                    val intersection = list.reduce { acc, set -> acc.intersect(set) as MutableSet<Char> }
                    if (intersection.isNotEmpty())
                        currentSet.addAll(intersection)
                    else
                        currentSet.addAll(list.reduce { acc, set -> acc.union(set) as MutableSet<Char> })
                }
                labels[neighbor] = entry
                queue.add(neighbor)
                if (unprocessed == 0 && neighbor.genome.size == 0)
                    labelingQueue.add(neighbor)
            }

        }
    }

    val visited = mutableSetOf<Node>()
    labelingQueue.forEach { node ->
        visited.add(node)

        val genome = node.genome as MutableGenome
        assert(genome.isEmpty) { "genome must be empty" }
        val array = labels[node]!!

        genome.add(buildString(length) {
            array.forEach { set ->
                this.append(set.random(GlobalExecutionSettings.RANDOM))
            }
        })
    }

    while (labelingQueue.isNotEmpty()) {
        val node = labelingQueue.pop()
        val nodeGenome = node.genome.primary

        node.neighbors.filter { it !in visited && it.genome.isEmpty }.forEach { neighbor ->
            val array = labels[neighbor]!!
            val genome = neighbor.genome as MutableGenome
            genome.add(buildString(length) {
                nodeGenome.forEachIndexed { index, c ->
                    this.append(if (c in array[index]) c else array[index].random(GlobalExecutionSettings.RANDOM)) // TODO: it works not so well
                }
            })

            visited.add(neighbor)
            labelingQueue.add(neighbor)
        }
    }

    return this
}

typealias DistanceMatrix = Map<Node, Map<Node, Int>>

val Cluster.distanceMatrix: DistanceMatrix
    get() {
        val result = mutableMapOf<Node, Map<Node, Int>>()
        terminals.forEach { terminalNode ->
            result[terminalNode] = terminalNode.computeGraphDistances()
        }
        return result
    }

val RootedTopology.distanceMatrix: DistanceMatrix
    get() = topology.cluster.distanceMatrix

fun DistanceMatrix.toArrays(): Array<IntArray> {
    val comparator = Comparator<Node> { o1, o2 -> o1!!.taxon.id.compareTo(o2!!.taxon.id) }
    val sorted = this.toSortedMap(comparator)
    return sorted.map { (_, distances) ->
        distances.toSortedMap(comparator).values.toIntArray()
    }.toTypedArray()
}

fun DistanceMatrix.print(): String {
    val comparator = Comparator<Node> { o1, o2 -> o1!!.taxon.id.compareTo(o2!!.taxon.id) }

    val sorted = this.toSortedMap(comparator)
    return buildString {
        append("${sorted.size}\n")
        for ((_, distances) in sorted) {
            val sortedDistances = distances.toSortedMap(comparator)
            append(sortedDistances.values.joinToString(separator = " ", postfix = "\n"))
        }
    }
}

fun Phylogeny.directed(): RootedPhylogeny {
    val root = branches.last().nodes.random(GlobalExecutionSettings.RANDOM)

    val queue = ArrayDeque<Node>()
    val visited = mutableSetOf<Node>()
    queue.add(root)
    visited.add(root)
    while (queue.isNotEmpty()) {
        val node = queue.pop()

        node.neighbors.forEach { neighbor ->
            if (!visited.contains(neighbor)) {
                node.next.add(neighbor)
                queue.add(neighbor)
                visited.add(neighbor)
            }
        }
    }
    return RootedPhylogeny(this, root)
}

fun RootedPhylogeny.label(): RootedPhylogeny {
    val labels = mutableMapOf<Node, Array<MutableSet<Char>>>()
    val length = phylogeny.cluster.terminals.first().genome.primary.length
    val labeled = phylogeny.cluster.filter { !it.genome.isEmpty }

    labeled.forEach { node ->
        val genome = node.genome.primary
        labels[node] = Array(length) { mutableSetOf(genome[it]) }
    }

    val notLabeled = mutableSetOf<Node>()
    notLabeled.addAll(phylogeny.cluster.filter { node -> node.genome.isEmpty })

    while (notLabeled.isNotEmpty()) {
        val toRemove = mutableListOf<Node>()
        notLabeled.forEach { node ->
            if (node.next.all { next -> !notLabeled.contains(next) }) {
                labels.computeIfAbsent(node) {
                    Array(length) { pos ->
                        val list = node.next.map { next -> labels[next]!![pos] }
                        val intersection = list.reduce { acc, set -> acc.intersect(set) as MutableSet<Char> }
                        if (intersection.isNotEmpty())
                            intersection
                        else
                            list.reduce { acc, set -> acc.union(set) as MutableSet<Char> }
                    }
                }
                toRemove.add(node)
            }
        }
        notLabeled.removeAll(toRemove)
    }

    val queue = ArrayDeque<Node>()
    val visited = mutableSetOf<Node>()
    queue.add(root)
    visited.add(root)

    root.genome.let {
        if (it.isEmpty) {
            val genome = it as MutableGenome
            val array = labels[root]!!

            genome.add(buildString(length) {
                array.forEach { set ->
                    this.append(set.random(GlobalExecutionSettings.RANDOM))
                }
            })
        }
    }

    while (queue.isNotEmpty()) {
        val node = queue.pop()
        val nodeGenome = node.genome.primary

        node.next.forEach { next ->
            if (!visited.contains(next)) {
                queue.add(next)
                visited.add(next)

                next.genome.let {
                    if (it.isEmpty) {
                        val nextArray = labels[next]!!
                        val genome = it as MutableGenome
                        genome.add(buildString(length) {
                            nextArray.forEachIndexed { index, set ->
                                if (nodeGenome[index] in set)
                                    append(nodeGenome[index])
                                else
                                    append(set.random(GlobalExecutionSettings.RANDOM))
                            }
                        })
                    }
                }
            }
        }
    }

    return this
}

fun Topology.toRooted(root: TopologyNode = nodes.random(GlobalExecutionSettings.RANDOM)): RootedTopology {
    val queue = ArrayDeque<Pair<TopologyNode, TopologyNode>>()
    val visited = mutableSetOf<TopologyNode>()
    queue.add(Pair(root, root))

    while (queue.isNotEmpty()) {
        val (node, prev) = queue.pop()
        visited.add(node)

        node.edges.forEach { edge ->
            if (edge.end != prev) {
                if (edge.end !in visited) {
                    queue.add(Pair(edge.end, node))
                    visited.add(edge.end)
                }
                node.next.add(edge)
            }
        }
    }

    return RootedTopology(this, root)
}

fun Topology.toRooted(root: Node): RootedTopology {
    val (edge, _) = edges.first { it.first.contains(root) }
    return when {
        edge.start.node === root -> toRooted(edge.start)
        edge.end.node === root -> toRooted(edge.end)
        else -> {
            val topologyNode = TopologyNode(root)
            remove(edge)
            val i = edge.nodes.indexOf(root)
            val firstEdge = Edge(edge.start, topologyNode, edge.nodes.subList(0, i + 1))
            val secondEdge = Edge(topologyNode, edge.end, edge.nodes.subList(i, edge.nodes.size))
            add(Pair(firstEdge, firstEdge.reversed()))
            add(Pair(secondEdge, secondEdge.reversed()))
            toRooted()
        }
    }
}

typealias Path = List<Node>

fun createEdge(v: Node, u: Node, directed: Boolean = false) {
    v.connect(u)
    if (!directed)
        u.connect(v)
}

val Node.genome: IGenome
    get() = taxon.genome

val TopologyNode.genome: IGenome
    get() = node.genome

fun Node.computeGraphDistances(): Map<Node, Int> {
    val result = hashMapOf<Node, Int>()
    if (isRealTaxon)
        result[this] = 0

    val queue = ArrayDeque<Pair<Node, Int>>()
    queue.add(Pair(this, 0))
    val visited = mutableSetOf<Node>(this)

    while (queue.isNotEmpty()) {
        val (currentNode, currentDistance) = queue.pop()
        for (neighbor in currentNode.neighbors) {
            if (neighbor !in visited) {
                visited.add(neighbor)
                val newDistance = currentDistance + 1
                if (neighbor.isRealTaxon)
                    result[neighbor] = newDistance
                queue.add(Pair(neighbor, newDistance))
            }
        }
    }
    return result
}

fun checkNode(node: Node, edges: List<Edge>): Boolean = !edges.any { edge -> edge.contains(node) }

fun Topology.checkInvariant(edgeList: List<Edge> = edges.map { it.first }): Boolean {
    val value = cluster.nodes.find { node -> checkNode(node, edgeList) }
    if (value != null)
        error("Can't find containing edge for $value")
    nodes.forEach {
        val neighbors = it.edges.map { it.end }
        val unique = neighbors.toSet().size
        if (neighbors.size != unique)
            error("Node $it has non-unique edges")
    }
    return true
}

fun RootedTopology.checkInvariant(): Boolean {
    val edgeList = edges
    if (edgeList.size != topology.edges.size)
        error("Edges number must be equal")

    topology.checkInvariant()
    topology.checkInvariant(edgeList)

    recombinationAmbassadors.forEach { ambassador ->
        if (!edgeList.any { it.contains(ambassador.recombination.firstParent) })
            error("Can not find containing edge for parent 1: ${ambassador.recombination.firstParent}")
        if (!edgeList.any { it.contains(ambassador.recombination.secondParent) })
            error("Can not find containing edge for parent 1: ${ambassador.recombination.secondParent}")
        if (!edgeList.any { it.contains(ambassador.recombination.child) })
            error("Can not find containing edge for parent 1: ${ambassador.recombination.child}")
    }

    return true
}

data class SplitResult(
        val node: TopologyNode,
        val inEdge: Edge,
        val revInEdge: Edge,
        val outEdge: Edge,
        val revOutEdge: Edge
)

fun Edge.split(node: Node): SplitResult {
    start.remove(this)
    val startNode = start
    end.removeIf { it.end === startNode }

    val index = nodes.indexOf(node)
    val newNode = TopologyNode(node)
    val firstPart = Edge(start, newNode, nodes.subList(0, index + 1))
    val secondPart = Edge(newNode, end, nodes.subList(index, nodes.size))

    start.add(firstPart, directed = true)
    newNode.add(secondPart, directed = true)

    val reversedSecondPart = secondPart.reversed()
    end.add(reversedSecondPart)
    val reversedFirstPart = firstPart.reversed()
    newNode.add(reversedFirstPart)

    return SplitResult(newNode, firstPart, reversedFirstPart, secondPart, reversedSecondPart)
}

fun RootedTopology.mergeTwoEdges(child: TopologyNode) {
    if (child.edges.size != 2 || child.next.size != 1)
        return
    if (recombinationEdges.any { it.start === child || it.end === child })
        return

    val outEdge = child.next.first()
    val inEdge = child.edges.first { it.end !== outEdge.end }

    val start = inEdge.end
    val end = outEdge.end

    start.removeIf { it.end === child }
    end.removeIf { it.end === child }
    topology.nodes.remove(child)

    topology.remove(inEdge)
    topology.remove(outEdge)

    val path = inEdge.nodes.reversed() + outEdge.nodes.drop(1)
    val newEdge = Edge(start, end, path)
    start.add(newEdge, directed = true)
    val revNewEdge = newEdge.reversed()
    end.add(revNewEdge)
    topology.add(Pair(newEdge, revNewEdge))
}

