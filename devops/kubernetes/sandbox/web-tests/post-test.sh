#!/usr/bin/env bash
set -euo pipefail

echo Running $0

set -x
mkdir -p /sandbox/reports/html

# Copy Allure report for publishing in Jenkins
cp -r /workspace/build/reports/allure-report/. /sandbox/reports/html

# Copy Allure results for publishing in Allure Server
cp -r /workspace/build/allure-results /sandbox/reports
