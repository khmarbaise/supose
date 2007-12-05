#!/bin/bash
# start script
export CP=`find lib -type f -name "*.jar" -exec echo -n {}: \;`

java -cp .:$CP com.soebes.supose.cli.SuposeCLI $*
