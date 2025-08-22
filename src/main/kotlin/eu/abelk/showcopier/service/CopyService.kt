package eu.abelk.showcopier.service

import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput
import com.github.fracpete.rsync4j.RSync
import eu.abelk.showcopier.logging.logger

class CopyService(
    private val destination: String,
    private val pathPrefix: String,
    private val dryRun: Boolean
) {
    private val logger = logger()

    fun copy(file: String) {
        val fullDestination = "${destination.stripSlashes()}/${file.removePrefix(pathPrefix).stripSlashes()}"
        if (dryRun) {
            logger.info("Would copy: $file -> $fullDestination")
        } else {
            logger.info("Copying: $file -> $fullDestination")
            val rsync = RSync()
                .source(file)
                .destination(fullDestination)
                .archive(true)
                .compress(true)
                .partial(true)
                .progress(true)
            val output = StreamingProcessOutput(object : StreamingProcessOwner {
                override fun getOutputType() =
                    StreamingProcessOutputType.BOTH

                override fun processOutput(line: String, stdout: Boolean) =
                    logger.info(
                        if (stdout) {
                            "[OUT] $line"
                        } else {
                            "[ERR] $line"
                        }
                    )
            })
            output.monitor(rsync.builder())
        }
    }

    fun String.stripSlashes() = removePrefix("/").removeSuffix("/")
}