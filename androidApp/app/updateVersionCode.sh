#!/bin/bash

# Set the file path
FILE="build.gradle.kts" # Or build.gradle if you're using Groovy

# Check if the file exists
if [ ! -f "$FILE" ]; then
  echo "Error: File '$FILE' not found."
  exit 1
fi

# Find the line containing versionCode and extract the current version number
VERSION_LINE=$(grep "versionCode =" "$FILE")
if [ -z "$VERSION_LINE" ]; then
  echo "Error: 'versionCode =' not found in '$FILE'."
  exit 1
fi

CURRENT_VERSION=$(echo "$VERSION_LINE" | sed 's/.*versionCode *= *//' | tr -d '\r')

# Check if the extracted version is a number
if ! [[ "$CURRENT_VERSION" =~ ^[0-9]+$ ]]; then
  echo "Error: Invalid versionCode format: '$CURRENT_VERSION'"
  exit 1
fi

# Increment the version number
NEW_VERSION=$((CURRENT_VERSION + 1))

# Replace the old line with the new one
sed -i "" "s/versionCode *= *$CURRENT_VERSION/versionCode = $NEW_VERSION/g" "$FILE"

echo "Updated versionCode from $CURRENT_VERSION to $NEW_VERSION in '$FILE'"
