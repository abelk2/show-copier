# show-copier

## Introduction
A small daemon that watches your Sonarr and Radarr series/movies,
filtering them for a specific tag. Matching shows will be
copied over to a pre-defined remote machine.

This is useful when you have Sonarr/Radarr on one machine with its
own Plex instance, and you also have a remote Plex server at another
location where you do not want separate Sonarr/Radarr.

## Prerequisites
The remote machine's show download directory needs to be exposed through HTTPS with
Basic Auth. Use nginx, Apache or any web server that supports sharing a directory.

## Deployment
```sh
# Build image to tarball (or download from GitHub release)
./gradlew jibBuildTar

# Copy tarball to the remote machine, then load it
docker load --input jib-image.tar

# Start the daemon
docker run -p 8080:8080 -d
  --restart=always \
  --name show-copier \
  -v /home/plex:/home/plex \
  -v /home/sc:/home/sc \
  abelk/show-copier:1.0.0 \
  --sonarr-url=https://sonarr.example.com/api/v3 \
  --sonarr-api-key= \
  --radarr-url=https://radarr.example.com/api/v3 \
  --radarr-api-key= \
  --download-base-url=https://example.com/shows \
  --download-username=user \
  --download-password= \
  --destination=/home/plex \
  --path-prefix=/mnt/example \
  --tag=test \
  --storage=/home/sc/storage.json \
  "--schedule=0 */5 * * *"
```

_Use the `--dry-run` switch to log which files would be copied and where without actually copying them. In this case
you might want to omit the `--schedule` which makes the copy run only once and quit._

## Supported arguments
```
Usage: <main class> [--dry-run] [--help] --destination=PATH
                    --download-base-url=URL --download-password=PASSWORD
                    --download-username=USERNAME
                    [--max-parallel-downloads=LIMIT] --path-prefix=PREFIX
                    --radarr-api-key=API-KEY --radarr-url=URL [--schedule=CRON]
                    --sonarr-api-key=API-KEY --sonarr-url=URL [--storage=FILE]
                    --tag=TAG
      --destination=PATH     the root directory to copy to, e.g. /path/to/root
      --download-base-url=URL
                             the base URL for downloading shows
      --download-password=PASSWORD
                             password for the download URL
      --download-username=USERNAME
                             username for the download URL
      --dry-run              list shows to copy without copying anything
      --help                 display a help message
      --max-parallel-downloads=LIMIT
                             the maximum number of parallel downloads
      --path-prefix=PREFIX   the prefix to cut from series/movie paths, e.g.
                               /mnt/shows
      --radarr-api-key=API-KEY
                             the API key for Radarr
      --radarr-url=URL       the base URL of Radarr API
      --schedule=CRON        cron expression for daemon mode (e.g. 0 */5 * *
                               *); omitting it will run the copy once and quit
      --sonarr-api-key=API-KEY
                             the API key for Sonarr
      --sonarr-url=URL       the base URL of Sonarr API
      --storage=FILE         the remote host
      --tag=TAG              the tag to filter for
```
