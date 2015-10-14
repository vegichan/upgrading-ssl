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

        try {
            serverSocket = new ServerSocket(9999);
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: 9999.");
            System.exit(1);
        }

        Socket clientSocket = null;
        System.out.println ("Waiting for connection.....");

        try {
            clientSocket = serverSocket.accept();
        }
        catch (IOException e)
        {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        System.out.println ("Connection successful");
        System.out.println ("Waiting for input.....");

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null)
        {
            if (inputLine.contains("UPGRADE")) {
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
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

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
} 