import socket, ssl, pprint
from sys import stdin

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(("localhost", 9999))
f = s.makefile("rb")

while True:
    print "input:",
    line = stdin.readline()
    s.send(line)

    if "UPGRADE" in line:
        ssl_sock = ssl.wrap_socket(s)
        print "upgraded to cipher suite:", ssl_sock.cipher()
        s = ssl_sock
        s.send("COMPLETE\n")
        f = s.makefile("rb")

    print "ECHO:", f.readline(),
s.close()
