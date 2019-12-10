#!/bin/sh
original="original_$1"
mv $1 $original
convert $original -resize '128x128>'  -gravity center -extent 128x128 $1