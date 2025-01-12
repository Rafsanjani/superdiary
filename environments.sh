#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Generate Android and shared secrets
echo "Generating Android and shared secrets..."
cat <<EOF > secrets.properties
OPENAI_KEY=$OPENAI_KEY
GOOGLE_SERVER_CLIENT_ID=$GOOGLE_SERVER_CLIENT_ID
MAPS_API_KEY=$MAPS_API_KEY_ANDROID
SUPABASE_KEY=$SUPABASE_KEY
SUPABASE_URL=$SUPABASE_URL
EOF

# Confirm generated secrets
echo "Generated secrets.properties:"
cat secrets.properties

# Generate iOS specific secrets
echo "Generating iOS secrets..."
cd iosApp

# Ensure Configurations directory exists
mkdir -p Configurations

# Create Release.xcconfig file. We don't generate Debug.xcconfig because we only build release on CI
cat <<EOF > Configurations/Release.xcconfig
SENTRY_BASE_URL = $SENTRY_BASE_URL_IOS
GIDClientID = $GIDCLIENTID
GIDServerClientID = $GOOGLE_SERVER_CLIENT_ID
GOOGLE_MAPS_SDK_KEY = $MAPS_API_KEY_IOS
EOF

cat <<EOF > Configurations/Debug.xcconfig
SENTRY_BASE_URL = $SENTRY_BASE_URL_IOS
GIDClientID = $GIDCLIENTID
GIDServerClientID = $GOOGLE_SERVER_CLIENT_ID
GOOGLE_MAPS_SDK_KEY = $MAPS_API_KEY_IOS
EOF

# Confirm generated iOS configuration
echo "Generated Configurations/Release.xcconfig:"
cat Configurations/Release.xcconfig
