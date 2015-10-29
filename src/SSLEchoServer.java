import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
public
class SSLEchoServer
{
    public static void main(String [] arstring)
    {
        try
        {
            System.setProperty("javax.net.ssl.keyStore","../../../certs/serverkey.p12");
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.keyStorePassword", "changeit");

            System.setProperty("javax.net.ssl.trustStore","../../../certs/TrustStore.jks");
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

            SSLServerSocketFactory sslserversocketfactory =
                    (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket sslserversocket =
                    (SSLServerSocket)sslserversocketfactory.createServerSocket(9999);

            sslserversocket.setWantClientAuth(true);

            SSLSocket sslsocket = (SSLSocket)sslserversocket.accept();
            InputStream inputstream = sslsocket.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            String string = null;
            while ((string = bufferedreader.readLine()) != null)
            {
                System.out.println(string);
                System.out.flush();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}