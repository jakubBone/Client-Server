package request;

import utils.UserInteraction;

import java.io.IOException;

public class RequestFactory {

    public Request createRequest(String requestName, UserInteraction userInteraction) throws IOException {
        switch (requestName.toUpperCase()) {
            case "REGISTER":
            case "LOGIN":
                String username = userInteraction.getUsername();
                String password = userInteraction.getPassword();
                return new LoginRegisterRequest(requestName, username, password);
            case "HELP":
                return new HelpRequest(requestName);
            case "WRITE":
                String recipient = userInteraction.getRecipient();
                String message = userInteraction.getMessage();
                return new WriteRequest(requestName, recipient, message);
            case "MAILBOX":
                String boxOperation = userInteraction.chooseBoxOperation();
                String mailbox = userInteraction.chooseMailBox();
                return new MailBoxRequest(requestName, boxOperation, mailbox);
            case "UPDATE":
                    return new AccountUpdateRequest(requestName);
            case "LOGOUT":
                return new LogoutRequest(requestName);
            default:
                return null;
        }
    }

    public Request createAccountUpdateRequest(UserInteraction userInteraction) throws IOException{
        String updateOperation = userInteraction.chooseAccountUpdateOperation();
        String userToUpdate = userInteraction.chooseUserToUpdate();
        String newPassword = null;
        if (updateOperation.equals("PASSWORD")) {
            newPassword = userInteraction.getNewPassword();
        }

        /*
        * TODO: Implement additional logic for 'DELETE' request
        */
        return new AccountUpdateRequest(updateOperation, userToUpdate, newPassword);
    }
}
