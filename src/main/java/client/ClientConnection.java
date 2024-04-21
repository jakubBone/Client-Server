
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.JsonConverter;

public class ClientConnection {
    private static final Logger logger = LogManager.getLogger(ClientConnection.class);
    private final int PORT_NUMBER = 5000;
    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private static boolean loggedIn = false;
    private boolean isAuthorized = false;

    public ClientConnection() {
        connectToServer();
    }

    public void connectToServer() {
        try {
            clientSocket = new Socket("localhost", PORT_NUMBER);
            outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger.info("Connection with Server established on port {}", PORT_NUMBER);
        } catch (IOException ex) {
            logger.error("Failed to establish connection with the server at port {}. Error: {}", PORT_NUMBER, ex.getMessage());
            retryConnection();
        }
    }

    private void retryConnection() {
        try {
            Thread.sleep(5000);
            logger.info("Attempting to reconnect to the server...");
            connectToServer();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.warn("Reconnection attempt interrupted", ie);
        }
    }

    public void sendRequest(String request) {
        System.out.println(request);
        outToServer.println(request);
        logger.debug("Sent request to server: {}", request);
    }

    /*public void readResponse() throws IOException {
        String response = null;
        while (!(response = inFromServer.readLine()).equals("<<END>>")) {
            if(response.equals("Login successful")){
                loggedIn = true;
            }
            if(response.equals("Operation succeeded: Authorized")){
                isAuthorized = true;
            }
            System.out.println(response);
        }
    }*/

    /*public void readResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while (!(line = inFromServer.readLine()).equals("<<END>>")) {
            response.append(line).append("\n");
            checkResponseStatus(line);
        }
        logger.debug("Received response from server: {}", response.toString());
        System.out.println(JsonResponse.fromJson(String.valueOf(response)));
    }*/


    public void readResponse() throws IOException {
        String response = null;
        while (!(response = inFromServer.readLine()).equals("<<END>>")) {
            checkResponseStatus(response);
            System.out.println(JsonConverter.fromJson(response));
        }
    }

    /*public void readResponse() throws IOException {
        String response = null;
        while (!(response = inFromServer.readLine()).equals("<<END>>")) {
            checkResponseStatus(response);
            System.out.println(response);
        }
    }*/

    private void checkResponseStatus(String response) {
        if (response.equals("Login successful") || response.equals("Registration successful")) {
            loggedIn = true;
            logger.info("User logged in successfully");
        }
        if (response.equals("Operation succeeded: Authorized")) {;
            isAuthorized = true;
            logger.info("User authorized for operations");
        }
    }

    public void disconnect() {
        try {
            if (outToServer != null) {
                outToServer.close();
            }
            if (inFromServer != null) {
                inFromServer.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            logger.info("Disconnected from server");
        } catch (IOException ex) {
            logger.error("Error during disconnection: {}", ex.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        ClientConnection.loggedIn = loggedIn;
        logger.info("Login status changed: {}", loggedIn);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}