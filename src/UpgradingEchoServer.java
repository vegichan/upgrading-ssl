import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;

public class UpgradingEchoServer
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = null;
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        PrintWriter out = null;
        BufferedReader in = null;

        try {
            serverSocket = new ServerSocket(9999);

            while (true) {
                System.out.println ("Waiting for connection.....");

                try {
                    Socket clientSocket = serverSocket.accept();


                    System.out.println("Connection successful");
                    System.out.println("Waiting for input.....");

                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("UPGRADE")) {

                            SSLSocket sslSocket = (SSLSocket) sslsocketfactory.createSocket(clientSocket, null,
                                    clientSocket.getPort(), false);
                            sslSocket.setUseClientMode(false);
                            clientSocket = sslSocket;
                            out = new PrintWriter(clientSocket.getOutputStream(), true);
                            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            inputLine = in.readLine();
                        }
                        out.println(inputLine);
                    }
                    clientSocket.close();
                } catch (Exception e) {
                    // empty
                }
            }
        }
        finally {
            if (out != null){
                out.close();
            }
            if (in != null){
                in.close();
            }
            if (serverSocket != null){
                serverSocket.close();
            }
        }

    }
} 