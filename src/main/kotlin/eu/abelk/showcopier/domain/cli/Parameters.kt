package eu.abelk.showcopier.domain.cli

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringExclude
import org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE
import picocli.CommandLine.Option
import java.io.File
import java.net.URI
import java.net.URL

class Parameters {
    @Option(
        names = ["--sonarr-url"],
        paramLabel = "URL",
        required = true,
        description = ["the base URL of Sonarr API"],
    )
    var sonarrUrl: URL = URI("https://sonarr.example.com/api/v3").toURL()

    @Option(
        names = ["--sonarr-api-key"],
        paramLabel = "API-KEY",
        required = true,
        description = ["the API key for Sonarr"]
    )
    @ToStringExclude
    var sonarrApiKey: String = ""

    @Option(
        names = ["--radarr-url"],
        paramLabel = "URL",
        required = true,
        description = ["the base URL of Radarr API"],
    )
    var radarrUrl: URL = URI("https://radarr.example.com/api/v3").toURL()

    @Option(
        names = ["--radarr-api-key"],
        paramLabel = "API-KEY",
        required = true,
        description = ["the API key for Radarr"]
    )
    @ToStringExclude
    var radarrApiKey: String = ""

    @Option(
        names = ["--destination"],
        paramLabel = "PATH",
        required = true,
        description = ["the root directory to copy to, e.g. /path/to/root"]
    )
    var destination: String = ""

    @Option(
        names = ["--path-prefix"],
        paramLabel = "PREFIX",
        required = true,
        description = ["the prefix to cut from series/movie paths, e.g. /mnt/shows"]
    )
    var pathPrefix: String = ""

    @Option(
        names = ["--download-base-url"],
        paramLabel = "URL",
        required = true,
        description = ["the base URL for downloading shows"]
    )
    var downloadBaseUrl: URL = URI("https://example.com/shows").toURL()

    @Option(
        names = ["--download-username"],
        paramLabel = "USERNAME",
        required = true,
        description = ["username for the download URL"]
    )
    var downloadUsername: String = ""

    @Option(
        names = ["--download-password"],
        paramLabel = "PASSWORD",
        required = true,
        description = ["password for the download URL"]
    )
    @ToStringExclude
    var downloadPassword: String = ""

    @Option(
        names = ["--storage"],
        paramLabel = "FILE",
        description = ["the remote host"]
    )
    var storageFile: File = File("show-copier.json")

    @Option(
        names = ["--tag"],
        paramLabel = "TAG",
        required = true,
        description = ["the tag to filter for"]
    )
    var tag: String = ""

    @Option(
        names = ["--dry-run"],
        description = ["list shows to copy without copying anything"]
    )
    var dryRun: Boolean = false

    @Option(
        names = ["--schedule"],
        paramLabel = "CRON",
        description = [
            "cron expression for daemon mode (e.g. 0 */5 * * *); omitting it will run the copy once and quit"
        ]
    )
    var schedule: String? = null

    @Option(
        names = ["--max-parallel-downloads"],
        paramLabel = "LIMIT",
        description = ["the maximum number of parallel downloads"]
    )
    var maxParallelDownloads: Int = 10

    @Option(
        names = ["--help"],
        usageHelp = true,
        description = ["display a help message"]
    )
    var helpRequested: Boolean = false

    override fun equals(other: Any?) =
        EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode() =
        HashCodeBuilder.reflectionHashCode(this)

    override fun toString(): String =
        ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE)
}
