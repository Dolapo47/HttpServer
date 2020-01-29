package com.dolapo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HttpServer {

    private static boolean quit = false;
    private static final int port = 8081;

    public static void main(String[] args) {

        System.out.printf("Listening for connection on port %d%n", port);

        Path path = Paths.get("src/com/dolapo/text.html");
        Path jsonPath = Paths.get("src/com/dolapo/package.json");

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept()) {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> jsonLines = Files.readAllLines(jsonPath,StandardCharsets.UTF_8);

            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            OutputStream ous = socket.getOutputStream();
            String message = "";
            int headers = 0;
            while (!quit) {
                String newMessage = null;
                String index = null;
                // when connecting with a browser, read all first before returning the response
                if (headers == 0) {
                    newMessage = br.readLine();
                    message += newMessage;
                    String[] methods = message.split(" ");
                    index = methods[1];
                }
                headers++;
                System.out.printf("Client: %s%n", message);
                    if (!socket.isClosed()) {
                        StringBuilder displayMessage = new StringBuilder();
                        if (index.equals("/")) {
                            for (String line : lines) {
                                displayMessage.append(line);
                            }
                        } else if (index.equals("/json")) {
                            for (String line : jsonLines) {
                                displayMessage.append(line);
                            }
                        }
                        ous.write(("HTTP/1.1 200 OK\r\n\r\n" + displayMessage + "\n").getBytes());
                        ous.flush();
                    } else {
                        System.out.println("Connection to client is closed!");
                    }
                quit = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
