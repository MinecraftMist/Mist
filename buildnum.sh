#!/bin/sh
REQ=$( curl -s -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/MinecraftMist/Mist/actions/runs?status=success&branch=$1 )
if [ $? -eq 0 ]; then
  KEYVAL=$( echo "$REQ" | head -n 2 )
  KEY=$( echo "$KEYVAL" | tail -c +6 | head -c 11 )
  if [ "$KEY" = "total_count" ]; then
    VAL=$( echo "$KEYVAL" | tail -c +20 | head -c -2 )
    echo "$VAL"
  else
    exit 102 # unknown response
  fi
else
  exit 101 # curl error
fi
