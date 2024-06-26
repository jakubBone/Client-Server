package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import request.*;
import shared.Screen;

/**
 * This class represents a client application for server communication.
 * It handles user login, sending requests, and interacting with a mailbox.
 */

@Log4j2
@Getter
@Setter
public class Client {
    private ClientConnection connection;
    private BufferedReader userInput;
    private static Gson gson;

    public static void main(String[] args) {
        Client client = new Client();
        client.handleServerCommunication();
    }

    public Client() {
        connection = new ClientConnection();
        gson = new Gson();
        userInput = new BufferedReader(new InputStreamReader(System.in));
        log.info("Client instance created");
    }

    public void handleServerCommunication() {
        try {
            log.info("Starting server communication");
            while(connection.isConnected()){
                printClientUI();
                String request = userInput.readLine();
                if (request == null || request.equalsIgnoreCase("EXIT")) {
                    connection.disconnect();
                    log.info("User exited the application");
                    return;
                }
                    handleRequest(request);
            }
        } catch (IOException ex) {
            log.error("Error in handling server communication: {}", ex.getMessage());
        }
    }


    /**
     * Processes a user request by sending it to the server.
     * Converts the user request into the appropriate format and sends it to the server.
     * @param request The user input request
     */
    public void handleRequest(String request) throws IOException {
        log.info("Handling user request: {}", request);
        try{
            RequestService requestService = new RequestService(connection);
            Request requestType = requestService.getRequest(request);

            if (requestType != null) {
                String jsonRequest = gson.toJson(requestType);
                connection.sendRequest(jsonRequest);
                log.info("User attempted to {}", request);
                connection.readResponse();
            } else {
                log.warn("Incorrect input from user: {}", request);
                System.out.println("Incorrect input. Please, try again");
            }
        } catch (IOException ex){
            log.error("Error handling request: {}", ex.getMessage());
        }
    }

    public void printClientUI(){
        if(!connection.isLoggedIn()) {
            Screen.printLoginMenu();
        } else {
            if(connection.isAuthorized()){
                Screen.printAdminMailBoxMenu();
            } else{
                Screen.printUserMailBoxMenu();
            }
        }
    }
}