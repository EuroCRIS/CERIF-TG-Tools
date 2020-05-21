#!/usr/bin/env bash

# Publish contents under cerif.eurocris.org/
# Usage: ./travis-publish.sh [file|directory ...]

openssl aes-256-cbc -K $encrypted_9f5c09747267_key -iv $encrypted_9f5c09747267_iv -in .ssh/cerif.eurocris.org.key.enc -out .ssh/cerif.eurocris.org.key -d && \
rsync -e 'ssh -i .ssh/cerif.eurocris.org-2.key -l jdvorak' -crz --delay-updates --delete-after --dry-run --itemize-changes "$@" www.eurocris.org:/data-eurocris/documentRoot/wwwcerif/