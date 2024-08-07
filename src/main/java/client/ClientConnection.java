package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import shared.ResponseStatus;
import shared.JsonConverter;

@Log4j2
@Getter
@Setter
public class ClientConnection {
    private final int PORT_NUMBER = 5000;
    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    public static boolean loggedIn = false;
    private boolean isAuthorized = false;
    public static int connectionAttempts = 0;
    private boolean connected = false;


    public ClientConnection() {
        connectToServer();

    }

    public void connectToServer() {
        try {
            clientSocket = new Socket("localhost", PORT_NUMBER);
            outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            connected = true;
            log.info("Connection with Server established on port {}", PORT_NUMBER);
        } catch (IOException ex) {
            log.error("Failed to establish connection with the server at port {}. Error: {}", PORT_NUMBER, ex.getMessage());
            retryConnection();
        }
    }

    public void retryConnection() {
        if (connectionAttempts >= 2) {
            log.error("Max reconnection attempts reached. Giving up");
            disconnect();
            return;
        }
        try {
            Thread.sleep(2000);
            log.info("Attempting to reconnect to the server...");
            Thread.sleep(5000);
            connectionAttempts++;
            connectToServer();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Reconnection attempt interrupted", ie);
        }
    }

    public void sendRequest(String request) {
        outToServer.println(request);
        log.info("Sent request to server: {}", request);
    }

    public void readResponse() throws IOException {
        try{
            String jsonResponse;
            log.info("Reading response from server");
            while (!"<<END>>".equals(jsonResponse = inFromServer.readLine())) {
                String response = JsonConverter.deserializeMessage(jsonResponse);
                checkResponseStatus(response);
                System.out.println("\nResponse: " + response);
            }
        } catch (IOException ex){
            log.error("Error reading response: {}", ex.getMessage());
        }
    }

    public void checkResponseStatus(String response) {
        ResponseStatus status = ResponseStatus.fromString(response);
        switch (status) {
            case ADMIN_LOGIN_SUCCEEDED:
                loggedIn = true;
                isAuthorized = true;
                log.info("Admin login succeeded");
                break;
            case USER_LOGIN_SUCCEEDED:
                loggedIn = true;
                log.info("User login succeeded");
                break;
            case REGISTRATION_SUCCESSFUL:
                loggedIn = true;
                log.info("User registered succeeded");
                break;
            case LOGOUT_SUCCEEDED:
                loggedIn = false;
                isAuthorized = false;
                log.info("User logout succeeded");
                break;
            case AUTHORIZATION_SUCCEEDED:
                isAuthorized = true;
                log.info("User authorization succeeded");
                break;
            case SWITCH_SUCCEEDED_USER_ROLE_ADMIN_ROLE:
                isAuthorized = true;
                log.info("Switch succeeded for user with admin role");
                break;
            case SWITCH_SUCCEEDED_USER_NON_ADMIN_ROLE:
                isAuthorized = false;
                log.info("Switch succeeded for user with non-admin role");
                break;
            default:
                log.info("No required to take an action");
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
            log.info("Client disconnected");
        } catch (IOException ex) {
            log.error("Error during disconnection: {}", ex.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isUserAuthorized() {
        return isAuthorized;
    }

    public boolean isConnected(){
        return connected;
    }
}