package operations;

import lombok.extern.log4j.Log4j2;
import user.User;
import user.UserManager;

import java.io.IOException;

@Log4j2
public class CredentialHandler {

     public String getAuthResponse(String command, String username, String password, UserManager userManager) throws IOException {
        String response = null;
        switch (command) {
            case "REGISTER":
                log.info("Registration attempted for user: {}", username);
                String registerStatus = userManager.register(username, password);
                if(registerStatus.equals("User exist")){
                    response = "Login failed: Existing user";
                } else {
                    response = OperationResponses.REGISTRATION_SUCCESSFUL.getResponse();
                }
                break;
            case "LOGIN":
                User user = userManager.login(username, password);
                if (user != null) {
                    log.info("User logged in successfully: {}", username);
                    UserManager.currentLoggedInUser = user;
                    response = OperationResponses.LOGIN_SUCCESSFUL.getResponse();
                } else {
                    log.warn("Login attempt failed for user: {}", username);
                    response = OperationResponses.LOGIN_FAILED.getResponse();
                }
                break;
        }
        return response;
    }

    public String getLogoutResponse(UserManager userManager) {
        userManager.logoutCurrentUser();
        return OperationResponses.SUCCESSFULLY_LOGGED_OUT.toString();
    }
}
