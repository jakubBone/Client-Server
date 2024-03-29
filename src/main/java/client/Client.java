package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private final int PORT_NUMBER = 5000;
    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private BufferedReader userInput;

    public static void main(String[] args) {
        Client client = new Client();
    }

    public Client(){
        connectToServer();
        handleServerCommunication();
    }

    public void connectToServer() {
        try {
            clientSocket = new Socket("localhost", PORT_NUMBER);
            logger.info("Connection with Server established");
        } catch (IOException ex){
            logger.error("Error - establishing connection with server.Server", ex);
        }
    }

    public void handleServerCommunication(){
        try {
            outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            userInput = new BufferedReader(new InputStreamReader(System.in));

            String request;
            System.out.print("\nType \"HELP\" to enter COMMANDS MENU: ");

            while(true) {
                request = userInput.readLine();
                if(request == null) {
                    break;
                }
                request = request.toUpperCase();

                outToServer.println(request);
                if(request.equals("STOP")){
                    disconnect();
                    break;
                }

                StringBuilder response = new StringBuilder();
                String line;
                while (!(line = inFromServer.readLine()).equals("<<END>>")) {
                    response.append(line + "\n");
                }
                System.out.print(response.toString());
                System.out.print("\nType next command:");
            }
        } catch (IOException ex){
            logger.error("Error - handling server communication", ex);
        }
    }

    public void disconnect() {
        try {
            outToServer.close();
            inFromServer.close();
            clientSocket.close();
            logger.info("Connection stopped");
        } catch (IOException ex) {
            logger.error("Error - disconnecting", ex);
        }
    }
}