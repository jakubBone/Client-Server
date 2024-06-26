package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.jupiter.api.*;
import shared.ResponseMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientCommunicationTest {
    static ClientConnection clientConnection;
    Socket mockSocket;
    PrintWriter mockOutToServer;
    BufferedReader mockInFromServer;

    @BeforeEach
    void setUp() {
        mockSocket = mock(Socket.class);
        mockOutToServer = mock(PrintWriter.class);
        mockInFromServer = mock(BufferedReader.class);
        clientConnection = new ClientConnection();
        clientConnection.setClientSocket(mockSocket);
        clientConnection.setOutToServer(mockOutToServer);
        clientConnection.setInFromServer(mockInFromServer);
    }

    @AfterAll
    static void closeDown() {
        clientConnection.disconnect();
    }

    @Test
    @DisplayName("Should test connecting to server")
    void testConnectToServer()  {
        Assertions.assertTrue(clientConnection.isConnected());
    }

    @Test
    @DisplayName("Should test sending request")
    void testSendRequest() {
        String request = "exampleRequest";

        clientConnection.sendRequest(request);

        // Verify that the request was sent to the server
        verify(mockOutToServer).println(request);
    }

    @Test
    @DisplayName("Should test checking response status")
    void testReadResponse() throws IOException {
        String jsonResponse1 = "{\"message\":\"response1\"}";
        String jsonResponse2 = "{\"message\":\"response2\"}";

        // Mock server responses
        when(mockInFromServer.readLine())
                .thenReturn(jsonResponse1, jsonResponse2, "<<END>>");

        clientConnection.readResponse();

        // Verify that the responses were read from the server
        verify(mockInFromServer, times(3)).readLine();
    }

    @Test
    @DisplayName("Should check response status")
    void testCheckResponseStatus() {
        // Test admin login succeeded
        String response = ResponseMessage.ADMIN_LOGIN_SUCCEEDED.getResponse();
        clientConnection.checkResponseStatus(response);
        assertTrue(clientConnection.isLoggedIn());
        assertTrue(clientConnection.isAuthorized());

        // Test user login succeeded
        response = ResponseMessage.USER_LOGIN_SUCCEEDED.getResponse();
        clientConnection.checkResponseStatus(response);
        assertTrue(clientConnection.isLoggedIn());

        // Test registration succeeded
        response = ResponseMessage.REGISTRATION_SUCCESSFUL.getResponse();
        clientConnection.checkResponseStatus(response);
        assertTrue(clientConnection.isLoggedIn());

        // Test logout succeeded
        response = ResponseMessage.LOGOUT_SUCCEEDED.getResponse();
        clientConnection.checkResponseStatus(response);
        assertFalse(clientConnection.isLoggedIn());
        assertFalse(clientConnection.isAuthorized());
        assertFalse(clientConnection.isAdminSwitchedAndAuthorized());

        // Test authorization succeeded
        response = ResponseMessage.AUTHORIZATION_SUCCEEDED.getResponse();
        clientConnection.checkResponseStatus(response);
        assertTrue(clientConnection.isAuthorized());

        // Test operation failed response
        response = ResponseMessage.SWITCH_SUCCEEDED.getResponse();
        clientConnection.checkResponseStatus(response);
        assertFalse(clientConnection.isAuthorized());
        assertTrue(clientConnection.isAdminSwitchedAndAuthorized());
    }
}