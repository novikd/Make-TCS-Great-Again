package ru.ifmo.ctd.novik.phylogeny.utils

import ru.ifmo.ctd.novik.phylogeny.common.*
import ru.ifmo.ctd.novik.phylogeny.io.output.GraphvizOutputClusterVisitor
import ru.ifmo.ctd.novik.phylogeny.tree.Edge
import ru.ifmo.ctd.novik.phylogeny.tree.Node
import ru.ifmo.ctd.novik.phylogeny.tree.Topology
import ru.ifmo.ctd.novik.phylogeny.tree.TopologyNode
import java.util.*

fun Cluster.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)
fun Topology.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)
fun RootedPhylogeny.toGraphviz(): String = GraphvizOutputClusterVisitor().visit(this)

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

    return Topology(hubs.values.toList(), edges)
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
                if (unprocessed == 0)
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
                this.append(set.random())
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
                    this.append(if (c in array[index]) c else array[index].random()) // TODO: it works not so well
                }
            })

            visited.add(neighbor)
            labelingQueue.add(neighbor)
        }
    }

    return this
}

fun Phylogeny.directed(): RootedPhylogeny {
    val root = branches.last().nodes.random()

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
                    this.append(set.random())
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
                                    append(set.random())
                            }
                        })
                    }
                }
            }
        }
    }

    return this
}

typealias Path = MutableList<Node>

fun createEdge(v: Node, u: Node, directed: Boolean = false) {
    v.connect(u)
    if (!directed)
        u.connect(v)
}

val Node.genome: IGenome
    get() = taxon.genome

fun Node.computeAllGraphDistances(): Map<Node, Int> {
    val result = hashMapOf<Node, Int>()
    result[this] = 0

    val queue: Queue<Node> = ArrayDeque()
    queue.add(this)

    while (queue.isNotEmpty()) {
        val currentNode = queue.poll()
        val currentDistance = result[currentNode]!!
        for (neighbor in currentNode.neighbors) {

            if (!result.contains(neighbor)) {
                result[neighbor] = currentDistance + 1
                queue.add(neighbor)
            }
        }
    }

    return result
}

fun Node.computeGraphDistances(): Map<Node, Int> = this.computeAllGraphDistances().filterKeys { x -> x.isRealTaxon }
