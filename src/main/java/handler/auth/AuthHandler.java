package handler.auth;

import lombok.extern.log4j.Log4j2;
import shared.ResponseMessage;
import user.manager.UserManager;

/**
 * The AuthHandler class processes authentication requests such as REGISTER and LOGIN.
 * It interacts with the UserManager to handle these operations and generate appropriate responses.
 */

@Log4j2
public class AuthHandler {

    /**
     * Processes authentication requests (REGISTER, LOGIN) and generates appropriate responses.
     * @param requestCommand The command for the request (e.g., "REGISTER", "LOGIN")
     */
     public String getResponse(String requestCommand, String username, String password, UserManager userManager)  {
         log.info("Processing authentication request: {}", requestCommand);
         switch (requestCommand) {
            case "REGISTER":
                return userManager.registerAndGetResponse(username, password);
            case "LOGIN":
                return userManager.loginAndGetResponse(username, password);
             default:
                 log.warn("Unknown request: {}", requestCommand);
                 return ResponseMessage.UNKNOWN_REQUEST.getResponse();
        }
    }
}