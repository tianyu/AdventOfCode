import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.counted
import com.github.ajalt.clikt.parameters.options.option
import y2016.Y2016
import y2021.Y2021

fun main(vararg args: String) = AdventOfCode.main(args)

object AdventOfCode: CliktCommand() {
  val verbosity by option("-v").counted()

  init {
    subcommands(Y2016, Y2021)
  }

  override fun run() {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", when (verbosity) {
      0 -> "ERROR"
      1 -> "INFO"
      2 -> "DEBUG"
      else -> "TRACE"
    })
  }
}