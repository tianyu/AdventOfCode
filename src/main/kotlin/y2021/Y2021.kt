package y2021

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

object Y2021: NoOpCliktCommand(name = "2021") {
  init {
    subcommands(Day1, Day2, Day3)
  }
}