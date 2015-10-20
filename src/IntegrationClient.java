import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import javax.xml.bind.DatatypeConverter;

public class IntegrationClient {
    static private int readLength(InputStream stream, byte[] lengthBuffer) throws IOException {
        int remaining = 4;
        while (remaining > 0) {
            int lengthRead = stream.read(lengthBuffer, 4 - remaining, remaining);

            if (lengthRead == -1) {
                throw new IOException("End of stream reached");
            }

            remaining -= lengthRead;
        }
        return getLength(lengthBuffer);
    }


    public static int getLength(byte[] buf) {
        int len = 0;
        for (int n = 0; n < 4; ++n) {
            len = (len << 8) | (buf[n] & 0xff);
        }
        return len;
    }

    static private void write(String encodedMessage, Socket echoSocket) throws IOException{
        byte[] enc = encodedMessage.getBytes();

        byte[] msg = new byte[enc.length + 4];
        writeLength(enc.length, msg);
        System.arraycopy(enc, 0, msg, 4, enc.length);

        String helloHex = DatatypeConverter.printHexBinary(msg);
        System.out.printf("hex: 0x%s\n", helloHex);

        echoSocket.getOutputStream().write(msg);
    }

    static private void writeLength(int messageLength, byte[] lengthBuffer) {
        for (int n = 3, length = messageLength; n >= 0; --n, length >>= 8) {
            lengthBuffer[n] = (byte) length;
        }
    }

    static private byte[] readMessage(Socket echoSocket) throws IOException {
        int length = readLength(echoSocket.getInputStream(), new byte[4]);

        byte[] bytes = new byte[length];
        int remaining = length;
        while (remaining > 0) {
            int read = echoSocket.getInputStream().read(bytes, length - remaining, remaining);
            if (read <= 0) {
                throw new IOException("Read failed");
            }
            remaining -= read;
        }

        return bytes;
    }

    public static void main(String[] args) throws IOException {

        String serverHostname = new String("127.0.0.1");

        if (args.length > 0)
            serverHostname = args[0];
        System.out.println("Attempting to connect to host " +
                serverHostname + " on port 48004.");

        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try (Socket echoSocket = new Socket(serverHostname, 48004)) {

            String encodedMessage = "<Authorize TargetService=\"Account\" Type=\"TLS\"/>";

            write(encodedMessage,echoSocket);


            try (SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(echoSocket,
                    null, echoSocket.getPort(), false)) {

                sslsocket.setUseClientMode(false);

                String[] supported = sslsocket.getSupportedCipherSuites();
                String[] anonCipherSuitesSupported = new String[supported.length];
                int count = 0;

                for(int i = 0; i < supported.length; i++)
                {
                    if(supported[i].indexOf("_anon_") > 0)
                    {
                        anonCipherSuitesSupported[count++] = supported[i];
                    }
                }

                String[] oldEnabled = sslsocket.getEnabledCipherSuites();
                String[] newEnabled = new String[oldEnabled.length + count];
                System.arraycopy(oldEnabled, 0, newEnabled, 0, oldEnabled.length);
                System.arraycopy(anonCipherSuitesSupported, 0, newEnabled, oldEnabled.length, count);
                sslsocket.setEnabledCipherSuites(newEnabled);

                //out = new PrintWriter(sslsocket.getOutputStream(), true);

                String s = "<TLSRequest Username=\"User\" Password=\"secure\"/>";

                write(s, sslsocket);
            }
        }
    }
}
