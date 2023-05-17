#!/usr/bin/env bash
set -euo pipefail

echo Running $0

set -x
cd /workspace

MOON_CLOUD_USER=worldremit moonSessionCount=3 ./gradlew -PmaxRetries=2 -Penv=pipeline-sandbox-moon --continue frameworkTest allureReport
