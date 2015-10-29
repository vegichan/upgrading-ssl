import socket, ssl, pprint
from sys import stdin
from time import sleep


# python is unable to load P12 certificates nor JKS files
# use of PEM files is necessary

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s = ssl.wrap_socket(s,
                           keyfile="certs/broker1.key",
                           certfile="certs/broker1.crt",
                           ca_certs="certs/serverkey.crt",
                           cert_reqs=ssl.CERT_REQUIRED)

s.connect(("localhost", 9999))
f = s.makefile("rb")

while True:
    print "input: ",
    line = stdin.readline()
    s.send(line)
    sleep(1)
s.close()
