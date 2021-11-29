package y2016

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import mu.KotlinLogging
import java.io.InputStream
import java.lang.ClassLoader.getSystemResourceAsStream
import kotlin.math.absoluteValue

private val log = KotlinLogging.logger {  }

/**
 * You're airdropped near Easter Bunny Headquarters in a city somewhere.
 * "Near", unfortunately, is as close as you can get - the instructions on the
 * Easter Bunny Recruiting Document the Elves intercepted start here, and
 * nobody had time to work them out further.
 */
internal object Day1: NoOpCliktCommand() {
  init {
    subcommands(Part1, Part2)
  }
}

/**
 * The Document indicates that you should start at the given coordinates
 * (where you just landed) and face North. Then, follow the provided sequence:
 * either turn left (L) or right (R) 90 degrees, then walk forward the given
 * number of blocks, ending at a new intersection.
 *
 * There's no time to follow such ridiculous instructions on foot, though, so
 * you take a moment and work out the destination. Given that you can only
 * walk on the street grid of the city, how far is the shortest path to the
 * destination?
 *
 * For example:
 *
 * * Following R2, L3 leaves you 2 blocks East and 3 blocks North, or 5
 * blocks away.
 * * R2, R2, R2 leaves you 2 blocks due South of your starting position,
 * which is 2 blocks away.
 * * R5, L5, R5, R3 leaves you 12 blocks away.
 *
 * How many blocks away is Easter Bunny HQ?
 */
private object Part1: CliktCommand() {
  val map: String by argument().default("y2016/day1.txt")

  override fun run() {
    var position = Vector(0, 0)
    log.info { "position: $position" }
    var direction = Vector(0, 1)
    log.debug { "facing: $direction" }

    getSystemResourceAsStream(map)!!.navigate { turn, speed ->
      direction *= turn
      log.debug { "facing: $direction" }
      position += direction * speed
      log.info { "moved to: $position" }
    }
    println("$map: ${position.distance}")
  }
}

/**
 * Then, you notice the instructions continue on the back of the Recruiting
 * Document. Easter Bunny HQ is actually at the first location you visit
 * twice.
 *
 * For example, if your instructions are R8, R4, R4, R8, the first location
 * you visit twice is 4 blocks away, due East.
 *
 * How many blocks away is the first location you visit twice?
 */
private object Part2: CliktCommand() {
  val map: String by argument().default("y2016/day1.txt")

  override fun run() {
    var position = Vector(0, 0) // starting at origin
    log.info { "starting position: $position" }
    var direction = Vector(0, 1) // facing north
    log.debug { "starting direction: $direction" }
    val history = HashSet<Vector>()
    history.add(position)
    getSystemResourceAsStream(map)!!.navigate { turn, speed ->
      direction *= turn
      log.debug { "facing: $direction" }
      for (i in 0 until speed) {
        position += direction
        log.info { "moved to: $position" }
        if (!history.add(position)) {
          println("$map: ${position.distance}")
          return
        }
      }
    }
    println("$map: No solution")
  }
}

private data class Vector(val x: Int, val y: Int) {
  override fun toString(): String = "($x, $y)"
}

private operator fun Vector.plus(that: Vector) = Vector(x + that.x, y + that.y)
private operator fun Vector.times(scalar: Int) = Vector(x * scalar, y * scalar)
private val Vector.distance get() = x.absoluteValue + y.absoluteValue

private data class Matrix(val a: Int, val b: Int, val c: Int, val d: Int)

private operator fun Vector.times(matrix: Matrix): Vector {
  val (a, b, c, d) = matrix
  return Vector(a * x + c * y, b * x + d * y)
}

private val left = Matrix(
  0, 1,
  -1, 0,
)

private val right = Matrix(
  0, -1,
  1, 0,
)

private inline fun InputStream.navigate(action: (Matrix, Int) -> Unit) = reader().useLines { lines ->
  lines
    .flatMap { it.splitToSequence(", ") }
    .forEach {
      log.info { "navigate: $it" }
      val turn = when (it[0]) {
        'L' -> left
        'R' -> right
        else -> {
          log.warn { "Unable to navigate: $it" }
          return@forEach
        }
      }
      val speed = it.substring(1).toInt()
      action(turn, speed)
    }
}