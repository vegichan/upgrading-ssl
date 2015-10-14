import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.*;

public class UpgradingEchoClient {
    public static void main(String[] args) throws IOException {

        String serverHostname = new String ("127.0.0.1");

        if (args.length > 0)
            serverHostname = args[0];
        System.out.println ("Attemping to connect to host " +
                serverHostname + " on port 9999.");

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // echoSocket = new Socket("taranis", 7);
            echoSocket = new Socket(serverHostname, 9999);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: " + serverHostname);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput;

        System.out.print ("input: ");
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);

            if (userInput.equals("UPGRADE")){
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                SSLSocket sslsocket = (SSLSocket)sslsocketfactory.createSocket(echoSocket, null, echoSocket.getPort(), false);
                //sslsocket.startHandshake();
                echoSocket = sslsocket;
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        echoSocket.getInputStream()));
                out.println("COMPLETE");
            }


            System.out.println("echo: " + in.readLine());
            System.out.print ("input: ");
        }

        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}
