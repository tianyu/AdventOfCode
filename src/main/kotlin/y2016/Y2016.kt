package y2016

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

object Y2016: NoOpCliktCommand(name = "2016") {
  init {
    subcommands(
      Day1
    )
  }
}