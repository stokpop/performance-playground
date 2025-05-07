#!/bin/bash
# mtls-setup.sh - Script to generate all certificates for mutual TLS

# Exit on error
set -e

# Configuration variables - change as needed
PASSWORD="changeit"
VALIDITY=3650
OUTPUT_DIR="generated-certs"
CA_NAME="Test CA"
SERVER_NAME="localhost"
CLIENT_NAME="client"
ORG="MyOrg"
ORG_UNIT="Development"
LOCALITY="Amsterdam"
STATE="NH"
COUNTRY="NL"

# Create output directory
mkdir -p $OUTPUT_DIR
cd $OUTPUT_DIR

# Print section header
section() {
  echo ""
  echo "========================================"
  echo "  $1"
  echo "========================================"
}

section "Creating Certificate Authority (CA)"
keytool -genkeypair \
  -alias ca \
  -keyalg RSA \
  -keysize 2048 \
  -storetype JKS \
  -keystore ca.jks \
  -storepass $PASSWORD \
  -validity $VALIDITY \
  -dname "CN=$CA_NAME,OU=$ORG_UNIT,O=$ORG,L=$LOCALITY,ST=$STATE,C=$COUNTRY" \
  -ext BasicConstraints:critical=ca:true \
  -ext KeyUsage:critical=keyCertSign,cRLSign

# Export the CA certificate
keytool -exportcert \
  -alias ca \
  -file ca.crt \
  -keystore ca.jks \
  -storepass $PASSWORD

section "Creating Server Certificate"
# Generate server keypair
keytool -genkeypair \
  -alias server \
  -keyalg RSA \
  -keysize 2048 \
  -storetype JKS \
  -keystore server.jks \
  -storepass $PASSWORD \
  -validity $VALIDITY \
  -dname "CN=$SERVER_NAME,OU=Server,O=$ORG,L=$LOCALITY,ST=$STATE,C=$COUNTRY"

# Create Certificate Signing Request (CSR)
keytool -certreq \
  -alias server \
  -keystore server.jks \
  -storepass $PASSWORD \
  -file server.csr

# Sign the server certificate with the CA
keytool -gencert \
  -alias ca \
  -keystore ca.jks \
  -storepass $PASSWORD \
  -infile server.csr \
  -outfile server.crt \
  -validity $VALIDITY \
  -ext BasicConstraints=ca:false

# Import CA certificate to server keystore
keytool -importcert \
  -alias ca \
  -file ca.crt \
  -keystore server.jks \
  -storepass $PASSWORD \
  -noprompt

# Import signed certificate back to server keystore
keytool -importcert \
  -alias server \
  -file server.crt \
  -keystore server.jks \
  -storepass $PASSWORD

section "Creating Client Certificate"
# Generate client keypair
keytool -genkeypair \
  -alias client \
  -keyalg RSA \
  -keysize 2048 \
  -storetype JKS \
  -keystore client.jks \
  -storepass $PASSWORD \
  -validity $VALIDITY \
  -dname "CN=$CLIENT_NAME,OU=Client,O=$ORG,L=$LOCALITY,ST=$STATE,C=$COUNTRY"

# Create Certificate Signing Request (CSR)
keytool -certreq \
  -alias client \
  -keystore client.jks \
  -storepass $PASSWORD \
  -file client.csr

# Sign the client certificate with the CA
keytool -gencert \
  -alias ca \
  -keystore ca.jks \
  -storepass $PASSWORD \
  -infile client.csr \
  -outfile client.crt \
  -validity $VALIDITY \
  -ext BasicConstraints=ca:false

# Import CA certificate to client keystore
keytool -importcert \
  -alias ca \
  -file ca.crt \
  -keystore client.jks \
  -storepass $PASSWORD \
  -noprompt

# Import signed certificate back to client keystore
keytool -importcert \
  -alias client \
  -file client.crt \
  -keystore client.jks \
  -storepass $PASSWORD

section "Creating Server Truststore"
# Create a new truststore and import the CA certificate
keytool -importcert \
  -alias ca \
  -file ca.crt \
  -keystore server-truststore.jks \
  -storetype JKS \
  -storepass $PASSWORD \
  -noprompt

section "Creating Client Truststore"
# Create a new truststore and import the CA certificate
keytool -importcert \
  -alias ca \
  -file ca.crt \
  -keystore client-truststore.jks \
  -storetype JKS \
  -storepass $PASSWORD \
  -noprompt

section "Cleaning up temporary files"
# Clean up CSR files
rm -f server.csr client.csr

section "Certificate Generation Complete"
echo "The following files have been created in $OUTPUT_DIR:"
echo "- ca.jks: Certificate Authority keystore"
echo "- ca.crt: Certificate Authority public certificate"
echo "- server.jks: Server keystore with private key and certificate"
echo "- server.crt: Server public certificate"
echo "- client.jks: Client keystore with private key and certificate"
echo "- client.crt: Client public certificate"
echo "- server-truststore.jks: Server truststore with CA certificate"
echo "- client-truststore.jks: Client truststore with CA certificate"
echo ""
echo "Password for all keystores and truststores: $PASSWORD"
echo ""
echo "Next steps:"
echo "1. Place these files in your application's resources directory"
echo "2. Configure your server and client to use these certificates"
echo "3. Enable mutual TLS in your application"