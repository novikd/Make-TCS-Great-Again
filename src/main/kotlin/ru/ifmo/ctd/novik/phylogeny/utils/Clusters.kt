package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.tree.*
import java.util.*

fun Cluster.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)
fun Topology.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)
fun RootedPhylogeny.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)
fun RootedTopology.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)

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
                this.append(set.random(GlobalRandom))
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
                    this.append(if (c in array[index]) c else array[index].random(GlobalRandom)) // TODO: it works not so well
                }
            })

            visited.add(neighbor)
            labelingQueue.add(neighbor)
        }
    }

    return this
}

fun Phylogeny.directed(): RootedPhylogeny {
    val root = branches.last().nodes.random(GlobalRandom)

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
                    this.append(set.random(GlobalRandom))
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
                                    append(set.random(GlobalRandom))
                            }
                        })
                    }
                }
            }
        }
    }

    return this
}

fun Topology.toRooted(root: TopologyNode = nodes.random(GlobalRandom)): RootedTopology {
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

typealias Path = MutableList<Node>

fun createEdge(v: Node, u: Node, directed: Boolean = false) {
    v.connect(u)
    if (!directed)
        u.connect(v)
}

val Node.genome: IGenome
    get() = taxon.genome

val TopologyNode.genome: IGenome
    get() = node.genome

inline fun Node.computeAllGraphDistances(predicate: (Node.() -> Boolean) = { true }): Map<Node, Int> {
    val result = hashMapOf<Node, Int>()
    result[this] = 0

    val queue: Queue<Node> = ArrayDeque()
    queue.add(this)

    while (queue.isNotEmpty()) {
        val currentNode = queue.poll()
        val currentDistance = result[currentNode]!!
        for (neighbor in currentNode.neighbors) {
            if (!result.contains(neighbor) && predicate(neighbor)) {
                result[neighbor] = currentDistance + 1
                queue.add(neighbor)
            }
        }
    }

    return result
}

fun Node.computeGraphDistances(): Map<Node, Int> = this.computeAllGraphDistances().filterKeys { x -> x.isRealTaxon }

fun checkNode(node: Node, edges: List<Edge>): Boolean = !edges.any { edge -> edge.contains(node) }

fun Topology.checkInvariant(edgeList: List<Edge> = edges.map { it.first }): Boolean {
    val value = cluster.nodes.find { node -> checkNode(node, edgeList) }
    if (value != null)
        error("Can't find containing edge for $value")
    return true
}

fun RootedTopology.checkInvariant(): Boolean {
    val edgeList = edges
    if (edgeList.size != topology.edges.size)
        error("Edges number must be equal")

    topology.checkInvariant()
    topology.checkInvariant(edgeList)

    recombinationGroups.forEach { group ->
        group.elements.forEach { recombination ->
            if (checkNode(recombination.firstParent, edgeList)) {
                error("Can't find containing edge for parent1 ${recombination.firstParent}")
            }
            if (checkNode(recombination.secondParent, edgeList)) {
                error("Can't find containing edge for parent2 ${recombination.secondParent}")
            }
            if (checkNode(recombination.child, edgeList)) {
                error("Can't find containing edge for child ${recombination.child}")
            }
        }
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
    if (child.next.isEmpty() || child.edges.size != 2)
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

