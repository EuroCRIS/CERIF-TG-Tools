#!/usr/bin/env bash

# Publish contents under https://cerif.eurocris.org/
# Usage: ./travis-publish.sh varbasename file|directory ...
# where:
#   varbasename    ... the variable base name for the SSH private key decryption
#   file|directory ... the stuff to publish (rsync)
#
# This script only actually publishes the stuff from the `master` branch of the `EuroCRIS/CERIF-Vocabularies` repo.
# The encrypted SSH key shall be stored as .ssh/cerif.eurocris.org.key-"${varbasename}".enc

if [ "$TRAVIS_REPO_SLUG" == "EuroCRIS/CERIF-Vocabularies" ]
then
	if [ "$TRAVIS_BRANCH" == "master" ]
	then
		umask 077
		enc_varname=encrypted_"$1"_key
		iv_varname=encrypted_"$1"_iv
		key_filename=.ssh/cerif.eurocris.org.key-"$1".enc
		shift
		echo 'www.eurocris.org ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIBWjyxaYmuw2ojW4C4NDaBTYypeANNpsGJvzOl9cWvrZ' >>~/.ssh/known_hosts

		openssl aes-256-cbc -K "${!enc_varname}" -iv "${!iv_varname}" -in "$key_filename" -out .ssh/cerif.eurocris.org.key -d && \
		rsync -e 'ssh -i .ssh/cerif.eurocris.org.key -l jdvorak' -crz --delay-updates --delete-after --dry-run --itemize-changes "$@" www.eurocris.org:/data-eurocris/documentRoot/wwwcerif/
	else
		echo "The branch (=${TRAVIS_BRANCH}) is not master, so not actually publishing anything"
	fi
else
	echo "The repo (=${TRAVIS_REPO_SLUG}) is not EuroCRIS/CERIF-Vocabularies, so not actually publishing anything"
fi