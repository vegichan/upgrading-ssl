# upgrading-ssl

## running

java -Djavax.net.ssl.keyStore=foobar -Djavax.net.ssl.keyStorePassword=foobar UpgradingEchoServer &

java -Djavax.net.ssl.trustStore=foobar -Djavax.net.ssl.trustStorePassword=foobar UpgradingEchoClient
