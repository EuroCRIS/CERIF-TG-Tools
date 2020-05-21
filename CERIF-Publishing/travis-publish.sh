#!/usr/bin/env bash

# Publish contents under cerif.eurocris.org/
# Usage: ./travis-publish.sh varbasename [file|directory ...]

enc_varname=encrypted_"$1"_key
iv_varname=encrypted_"$1"_iv
key_filename=.ssh/cerif.eurocris.org.key-"$1".enc
shift
openssl aes-256-cbc -K "${!enc_varname}" -iv "${!iv_varname}" -in "$key_filename" -out .ssh/cerif.eurocris.org.key -d && \
rsync -e 'ssh -i .ssh/cerif.eurocris.org-2.key -l jdvorak' -crz --delay-updates --delete-after --dry-run --itemize-changes "$@" www.eurocris.org:/data-eurocris/documentRoot/wwwcerif/