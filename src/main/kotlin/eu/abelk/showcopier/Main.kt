package eu.abelk.showcopier

import dev.inmo.krontab.doInfinity
import eu.abelk.showcopier.domain.cli.Parameters
import eu.abelk.showcopier.logging.logger
import eu.abelk.showcopier.modules.Modules
import picocli.CommandLine
import picocli.CommandLine.PicocliException

private val logger = logger("Main")

suspend fun main(args: Array<String>) {
    val parameters = Parameters()
    try {
        val result = CommandLine(parameters).parseArgs(*args)
        CommandLine.printHelpIfRequested(result)

        if (parameters.schedule.isNullOrBlank()) {
            runCopy(parameters)
        } else {
            doInfinity(parameters.schedule!!) {
                runCopy(parameters)
            }
        }
    } catch (exception: PicocliException) {
        System.err.println(exception.message)
    }
}

private suspend fun runCopy(parameters: Parameters) =
    runCatching {
        Modules(parameters)
            .get<CopyCommand>()
            .run()
    }.onFailure { throwable ->
        logger.error("Failed to run copy", throwable)
    }
