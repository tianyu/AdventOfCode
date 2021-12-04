package y2021

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.types.enum
import mu.KotlinLogging
import java.lang.ClassLoader.getSystemResourceAsStream

object Day3: CliktCommand() {
  private val log = KotlinLogging.logger {  }
  private val part: Part by argument().enum<Part>().default(Part.Part1)
  private val input by argument().default("y2021/day3.txt")

  override fun run() = with(part) {
    log.info { "Report file: $input" }
    getSystemResourceAsStream(input).reader().useLines { analyze(it) }
  }

  @Suppress("unused")
  enum class Part {
    Part1 {
      override fun analyze(report: Sequence<String>) {
        log.info { "Analyzing power consumption" }
        var count = 0
        val oneCounts = IntArray(12)
        report.forEach { line ->
          count += 1
          line.forEachIndexed { index, bit ->
            when (bit) {
              '1' -> oneCounts[11 - index] += 1
              '0' -> {}
              else -> log.warn { "Bad bit on line $count, position $index: $bit" }
            }
          }
        }

        log.info { "count: $count" }
        log.info { "oneCounts: ${oneCounts.joinToString()}" }

        var gamma = 0
        oneCounts.forEachIndexed { index, ones ->
          gamma = gamma or when {
            ones > count - ones -> 1 shl index
            else -> 0
          }
        }

        log.info { "gamma rate: $gamma (${gamma.toString(2)})" }

        val epsilon = gamma.inv() and 0b111111111111
        log.info { "epsilon rate: $epsilon (${epsilon.toString(2)})" }

        println(gamma * epsilon)
      }
    },
    Part2 {
      override fun analyze(report: Sequence<String>) {
        log.info { "Analyzing life support rating" }
        var mask = 0b100000000000
        val (mostCommon, leastCommon) = report
          .map { it.toInt(2) }
          .partition { it and mask == mask }
          .mostToLeastCommon()

        mask = mask shr 1
        val o2GeneratorRating = mostCommon.o2GeneratorRating(mask)
        val co2ScrubberRating = leastCommon.co2ScrubberRating(mask)

        log.info { "02 generator rating: $o2GeneratorRating (${o2GeneratorRating.toBinaryString()})" }
        log.info { "C02 scrubber rating: $co2ScrubberRating (${co2ScrubberRating.toBinaryString()})" }
        println(o2GeneratorRating * co2ScrubberRating)
      }

      tailrec fun List<Int>.o2GeneratorRating(mask: Int): Int {
        log.info { "Computing 02 generator rating on: ${joinToString(limit = 5) { it.toBinaryString() }}" }
        if (size == 1) return first()
        log.info { "Finding most common values matching ${mask.toBinaryString()}" }
        val (mostCommon, _) = partition { it and mask == mask }.mostToLeastCommon()
        return mostCommon.o2GeneratorRating(mask shr 1)
      }

      tailrec fun List<Int>.co2ScrubberRating(mask: Int): Int {
        log.info { "Computing C02 generator rating on: ${joinToString(limit = 5) { it.toBinaryString() }}" }
        if (size == 1) return first()
        log.info { "Finding least common values matching ${mask.toBinaryString()}" }
        val (_, leastCommon) = partition { it and mask == mask }.mostToLeastCommon()
        return leastCommon.co2ScrubberRating(mask shr 1)
      }

      fun <T> Pair<List<T>, List<T>>.mostToLeastCommon() = when {
        first.size >= second.size -> this
        else -> second to first
      }

      fun Int.toBinaryString() = toString(2).padStart(12, '0')
    };

    abstract fun analyze(report: Sequence<String>)
  }
}