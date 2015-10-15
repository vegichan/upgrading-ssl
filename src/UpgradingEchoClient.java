import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;

public class UpgradingEchoClient {
    public static void main(String[] args) throws IOException {

        String serverHostname = new String("127.0.0.1");

        if (args.length > 0)
            serverHostname = args[0];
        System.out.println("Attempting to connect to host " +
                serverHostname + " on port 9999.");

        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try (Socket echoSocket = new Socket(serverHostname, 9999)) {

            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            System.out.print("input: ");
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);

                if (userInput.equals("UPGRADE")) {

                    SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(echoSocket,
                            null, echoSocket.getPort(), false);
                    out = new PrintWriter(sslsocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
                    out.println("COMPLETE");
                }


                System.out.println("echo: " + in.readLine());
                System.out.print("input: ");
            }
        }
    }
}
