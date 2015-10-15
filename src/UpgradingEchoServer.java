import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;

public class UpgradingEchoServer
{
    public static void main(String[] args) throws IOException
    {
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try (ServerSocket serverSocket = new ServerSocket(9999)){

            while (true) {
                System.out.println ("Waiting for connection.....");

                try (Socket clientSocket = serverSocket.accept()){


                    System.out.println("Connection successful");
                    System.out.println("Waiting for input.....");

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("UPGRADE")) {

                            SSLSocket sslSocket = (SSLSocket) sslsocketfactory.createSocket(clientSocket, null,
                                    clientSocket.getPort(), false);
                            sslSocket.setUseClientMode(false);
                            out = new PrintWriter(sslSocket.getOutputStream(), true);
                            in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
                            inputLine = in.readLine();
                        }
                        out.println(inputLine);
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }


    }
} 