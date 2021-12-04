package y2021

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.types.enum
import mu.KotlinLogging
import java.lang.ClassLoader.getSystemResourceAsStream

object Day2: CliktCommand() {
  private val log = KotlinLogging.logger {  }
  private val part: Part by argument().enum<Part>(ignoreCase = true).default(Part.Part1)
  private val input by argument().default("y2021/day2.txt")

  override fun run() = with(part) {
    getSystemResourceAsStream(input).reader().useLines { commands ->
      val position = Position(0, 0, 0)
      commands.forEachCommand { direction, speed ->
        log.debug { "Moving $direction: $speed" }
        position.move(direction, speed)
        log.info { "Moved to $position" }
      }
      position.run { println(depth * distance) }
    }
  }

  data class Position(var depth: Int, var distance: Int, var aim: Int)

  @Suppress("unused")
  enum class Part {
    Part1 {
      override fun Position.move(direction: String, speed: Int) = when (direction) {
        "forward" -> distance += speed
        "down" -> depth += speed
        "up" -> depth -= speed
        else -> log.warn { "Cannot move $direction: $speed" }
      }
    },
    Part2 {
      override fun Position.move(direction: String, speed: Int) = when (direction) {
        "forward" -> {
          distance += speed
          depth += aim * speed
        }
        "down" -> aim += speed
        "up" -> aim -= speed
        else -> log.warn { "Cannot move $direction: $speed" }
      }
    };

    abstract fun Position.move(direction: String, speed: Int)
  }

  private fun Sequence<String>.forEachCommand(action: (direction: String, speed: Int) -> Unit) = forEach {
    val (direction, speed) = it.split(' ')
    action(direction, speed.toInt())
  }
}