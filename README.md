# show-copier

## Introduction
A small daemon that watches your Sonarr and Radarr series/movies,
filtering them for a specific tag. Matching shows will be
copied over to a pre-defined remote machine.

This is useful when you have Sonarr/Radarr on one machine with its
own Plex instance, and you also have a remote Plex server at another
location where you do not want separate Sonarr/Radarr.

## Prerequisites
The local machine has to be able to SSH into the remote one.

## Deployment
```sh
# Build image to tarball (or download from GitHub release)
./gradlew jibBuildTar

# Copy tarball to the remote machine, then load it
docker load --input jib-image.tar

# Start the daemon
docker run -p 8080:8080 -d --restart=always --name show-copier abelk/show-copier:1.0.0 \
  --sonarr-url=https://sonarr.example.com/api/v3 \
  --sonarr-api-key= \
  --radarr-url=https://radarr.example.com/api/v3 \
  --radarr-api-key= \
  --destination=user@example.com:/home/plex \
  --path-prefix=/mnt/example \
  --tag=test \
  "--schedule=0 */5 * * *"
```

_Use the `--dry-run` switch to log which files would be copied and where without actually copying them. In this case
you might want to omit the `--schedule` which makes the copy run only once and quit._

## Notes
- You might want to change the architecture in the jib config in the build file, it's set to `arm` currently
  (I use it on Raspberry Pi).
