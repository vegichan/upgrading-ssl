from OpenSSL import crypto, SSL
from socket import gethostname
from pprint import pprint
from time import gmtime, mktime
from os.path import exists, join
from subprocess import call
from shutil import rmtree
from os import makedirs

def create_self_signed_cert(CN, C_F, K_F):
    print "creating", C_F, K_F
    if not exists(C_F) or not exists(K_F):
        # create a key pair
        k = crypto.PKey()
        k.generate_key(crypto.TYPE_RSA, 1024)
        # create a self-signed cert
        cert = crypto.X509()
        cert.get_subject().C =  "IR"
        cert.get_subject().ST = "Dublin"
        cert.get_subject().L =  "Dublin"
        cert.get_subject().O =  "NuoDB"
        cert.get_subject().OU = "NuoDB"
        cert.get_subject().CN = CN
        cert.set_serial_number(1000)
        cert.gmtime_adj_notBefore(0)
        cert.gmtime_adj_notAfter(315360000)
        cert.set_issuer(cert.get_subject())

        cert.set_pubkey(k)

        cert.sign(k, 'sha1')

        open(C_F, "wt").write(
                crypto.dump_certificate(crypto.FILETYPE_PEM, cert))

        open(K_F, "wt").write(
                crypto.dump_privatekey(crypto.FILETYPE_PEM, k))

trust_name = "TrustStore"
cert_dir = "certs"
pass_txt = "changeit"

if exists(cert_dir):
    rmtree(cert_dir)

makedirs(cert_dir)

def generateKey(CN, isKeystore):
    cert_file = "%s.crt" % CN
    key_file = "%s.key" % CN
    store_file = "%s.jks" % trust_name
    server_file = "%s.jks" % CN
    pkcs_file = "%s.p12" % CN
    C_F = join(cert_dir, cert_file)
    K_F = join(cert_dir, key_file)
    T_F = join(cert_dir, store_file)
    P_F = join(cert_dir, pkcs_file)
    S_F = join(cert_dir, server_file)
    create_self_signed_cert(CN, C_F, K_F)

    enc_pass = "pass:%s" % pass_txt
    call_cmd = ["openssl" ,"pkcs12" ,"-export" ,"-in", C_F, "-inkey", K_F, "-out", P_F, "-name", CN, "-password", enc_pass]
    call(call_cmd)

    call_cmd = ["keytool", "-importcert", "-file", C_F, "-keystore", S_F, "-alias", CN, "-storepass", pass_txt, "-noprompt"]
    call(call_cmd)

    if not isKeystore:
        call_cmd = ["keytool", "-importcert", "-file", C_F, "-keystore", T_F, "-alias", CN, "-storepass", pass_txt, "-noprompt"]
        call(call_cmd)


generateKey("serverkey", True)
generateKey("broker1", False)
generateKey("broker2", False)
generateKey("broker3", False)
generateKey("broker4", False)
