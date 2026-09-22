#!/usr/bin/env bash
set -euo pipefail
: "${GH_TOKEN:?Set DEPS_TOKEN with Contents read access to TF-Minecraft/ServerAssets}"
ref=2fe025eb9879bab7dfe48e32395d8a8c1dff75f7
mkdir -p libs
curl --fail --location --silent --show-error --retry 3 -H "Authorization: Bearer $GH_TOKEN" -H "Accept: application/vnd.github.raw+json" "https://api.github.com/repos/TF-Minecraft/ServerAssets/contents/jars/4241c14a7727/gson-2.10.1.jar?ref=$ref" > "libs/gson-2.10.1.jar"
curl --fail --location --silent --show-error --retry 3 -H "Authorization: Bearer $GH_TOKEN" -H "Accept: application/vnd.github.raw+json" "https://api.github.com/repos/TF-Minecraft/ServerAssets/contents/jars/2d9484f4c649/json-simple-1.1.jar?ref=$ref" > "libs/json-simple-1.1.jar"
curl --fail --location --silent --show-error --retry 3 -H "Authorization: Bearer $GH_TOKEN" -H "Accept: application/vnd.github.raw+json" "https://api.github.com/repos/TF-Minecraft/ServerAssets/contents/jars/97e0c708c1e3/spigot-api-1.20.2-R0.1-SNAPSHOT.jar?ref=$ref" > "libs/spigot-api-1.20.2-R0.1-SNAPSHOT.jar"
sha256sum --check .github/dependencies.sha256
