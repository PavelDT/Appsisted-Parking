#!/bin/bash

# Script targets a windows environment and aims to download the needed
# infrastructure resources for the app-sisted project.
#
# Resource list:
# 1. Cassandra
#
# The script requires git-bash to execute.
# To run the script, install git-bash (https://gitforwindows.org/)
# and run the script by executing the below command:
# 1. start git-bash
# 2. cd <path/to/script>
# 3. ./infra-setup.sh

echo "Seting up infrastructure, downloading resources to `pwd`"

# Check if cassandra is already downloaded
if [ -d "apache-cassandra-4.0-beta3/" ]; then
  # download cassandra 4.0
  curl -LO https://www.mirrorservice.org/sites/ftp.apache.org/cassandra/4.0-beta3/apache-cassandra-4.0-beta3-bin.tar.gz
  # unarchive the gzipped file
  tar -xf apache-cassandra-4.0-beta3-bin.tar.gz
else
  echo "Cassandra already downloaed."
  # check if java is installed
  JAVA_PATH=`which java`
  if [[ -z $JAVA_PATH ]]
  then
    echo "no java detected"
  else
    echo "java installed in $JAVA_PATH"
    # Cassandra can be started.
    echo "Cassandra can safely be started via command:"
    echo "\"`pwd`/apache-cassandra-4.0-beta3/bin/cassandra -f\""
  fi
fi

# check for python
# based on https://stackoverflow.com/a/33183884
version=$(python -V 2>&1 | grep -Po '(?<=Python )(.+)')
if [[ -z "$version" ]]
then
    echo "Python not detected, please install python 3.6.x from https://www.python.org/downloads/release/python-360/"
else
    echo "Python version: $version"
    echo "'cqlsh' can be used as a client for cassandra"
fi
