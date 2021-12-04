package y2021

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.types.enum
import mu.KotlinLogging
import java.io.InputStream
import java.lang.ClassLoader.getSystemResourceAsStream
import java.util.*

object Day4: CliktCommand() {
  val log = KotlinLogging.logger {  }
  val part by argument().enum<Part>(ignoreCase = true).default(Part.Part1)
  val input by argument().default("y2021/day4.txt")

  override fun run() = with(part) {
    println(getSystemResourceAsStream(input).bingoGames().pickGame().score)
  }

  @Suppress("unused")
  enum class Part {
    Part1 {
      override fun Sequence<BingoGame>.pickGame(): BingoGame = minByOrNull { it.steps }!!
    },
    Part2 {
      override fun Sequence<BingoGame>.pickGame(): BingoGame = maxByOrNull { it.steps }!!
    };

    abstract fun Sequence<BingoGame>.pickGame(): BingoGame
  }

  data class BingoGame(val steps: Int, val score: Int)

  fun InputStream.bingoGames(): Sequence<BingoGame> = sequence {
    Scanner(this@bingoGames).use { read ->
      val draws = read.nextLine().split(',').map(String::toInt).toIntArray()
      log.info { "Drawn numbers: ${draws.joinToString(limit = 10, truncated = "...+${draws.size - 10} more")}" }

      while (read.hasNextInt()) {
        val board = (0 until 25).associateBy { read.nextInt() }
        log.info { "Board: ${board.keys}" }

        var steps = 0
        var score = board.keys.sum()
        val rowsAndCols = IntArray(10)
        for (draw in draws) {
          log.info { "Draw: $draw" }
          steps += 1
          val (row, col) = board[draw] ?: continue
          log.info { "Marked: $row, $col" }
          score -= draw
          rowsAndCols[row] += 1
          rowsAndCols[5 + col] += 1
          if (rowsAndCols.any { it == 5 }) {
            score *= draw
            log.info { "Won in $steps steps! Score: $score" }
            yield(BingoGame(steps, score))
            break
          }
        }
      }
    }
  }

  private operator fun Int.component1() = this / 5
  private operator fun Int.component2() = this % 5
}