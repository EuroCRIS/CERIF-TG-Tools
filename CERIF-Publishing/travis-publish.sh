#!/usr/bin/env bash

# Publish contents under https://cerif.eurocris.org/
# Usage: ./travis-publish.sh varbasename file|directory ...
# where:
#   varbasename    ... the variable base name for the SSH private key decryption
#   file|directory ... the stuff to publish via rsync
#
# This script only actually publishes the stuff from the `master` branch of a `EuroCRIS`-owned repo.
# The encrypted SSH key shall be stored as .ssh/cerif.eurocris.org.key-"${varbasename}".enc
#
# This script is intended to be called from
# https://github.com/EuroCRIS/CERIF-Vocabularies/.travis.yml and
# https://github.com/EuroCRIS/CERIF-DataModel/.travis.yml

if [ "${TRAVIS_REPO_SLUG%/*}" == "EuroCRIS" ]
then
	if [ "${TRAVIS_PULL_REQUEST_BRANCH:-$TRAVIS_BRANCH}" == "master" ]
	then
		umask 077
		enc_varname=encrypted_"$1"_key
		iv_varname=encrypted_"$1"_iv
		key_filename=.ssh/cerif.eurocris.org.key-"$1".enc
		shift
		echo 'www.eurocris.org ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIBWjyxaYmuw2ojW4C4NDaBTYypeANNpsGJvzOl9cWvrZ' >>~/.ssh/known_hosts

		openssl aes-256-cbc -K "${!enc_varname}" -iv "${!iv_varname}" -in "$key_filename" -out .ssh/cerif.eurocris.org.key -d && \
		rsync -e 'ssh -i .ssh/cerif.eurocris.org.key -l jdvorak' -crz --delay-updates --delete-after --itemize-changes "$@" www.eurocris.org:/data-eurocris/documentRoot/wwwcerif/
	else
		echo "The branch (=${TRAVIS_PULL_REQUEST_BRANCH:-$TRAVIS_BRANCH}) is not master, so not publishing anything"
	fi
else
	echo "The repo owner (=${TRAVIS_REPO_SLUG%/*}) is not EuroCRIS, so not publishing anything"
fi