package y2021

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.types.enum
import mu.KotlinLogging
import java.lang.ClassLoader.getSystemResourceAsStream

val log = KotlinLogging.logger {  }

object Day1: CliktCommand() {
  val part: Part by argument().enum(ignoreCase = true)
  val input by argument().default("y2021/day1.txt")

  @Suppress("unused")
  enum class Part {
    Part1 {
      override fun Sequence<Int>.preprocess(): Sequence<Int> = this
    },
    Part2 {
      override fun Sequence<Int>.preprocess(): Sequence<Int> =
        windowed(3, partialWindows = false, transform = List<Int>::sum)
    };

    abstract fun Sequence<Int>.preprocess(): Sequence<Int>
  }

  override fun run() = with(part) {
    getSystemResourceAsStream(input).reader().useLines { lines ->
      val numberOfDepthIncreases = lines
        .map(String::toInt)
        .preprocess()
        .zipWithNext { a, b ->
          log.debug { "$a -> $b: ${if (a > b) "increased" else "decreased"}" }
          if (b > a) 1 else 0
        }
        .sum()
      println(numberOfDepthIncreases)
    }
  }
}