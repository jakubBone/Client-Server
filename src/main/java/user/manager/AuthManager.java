package user.manager;

import lombok.extern.log4j.Log4j2;
import shared.ResponseStatus;
import user.credential.User;

@Log4j2
public class AuthManager {

    public String registerAndGetResponse(String username, String password, UserManager userManager) {
        log.info("Registration attempted for user: {}", username);
        User user = userManager.getUserDAO().getUserFromDB(username);

        if (user != null) {
            log.info("Registration attempt failed - user already exists: {}", username);
            return ResponseStatus.REGISTRATION_FAILED_USER_EXISTS.getResponse();
        }

        handleRegister(username, password, userManager);
        log.info("Registration successful for new user: {}", username);
        return ResponseStatus.REGISTRATION_SUCCESSFUL.getResponse();
    }

    public String loginAndGetResponse(String username, String password, UserManager userManager) {
        log.info("Login attempted for user: {}", username);
        User user = userManager.getUserDAO().getUserFromDB(username);
        if (user == null) {
            log.info("Login attempt failed - user does not exist: {}", username);
            return ResponseStatus.FAILED_TO_FIND_USER.getResponse();
        }

        if (!isPasswordCorrect(password, user, userManager)) {
            log.info("Incorrect password attempt for user: {}", user.getUsername());
            return ResponseStatus.LOGIN_FAILED_INCORRECT_PASSWORD.getResponse();
        }

        log.info("User password correct: {}", user.getUsername());

        handleLogin(user);

        log.info("User login succeeded: {}", user.getUsername());

        if (userManager.isUserAdmin()) {
            return ResponseStatus.ADMIN_LOGIN_SUCCEEDED.getResponse();
        } else {
            return ResponseStatus.USER_LOGIN_SUCCEEDED.getResponse();
        }
    }

    public void handleRegister(String username, String password, UserManager userManager) throws IllegalArgumentException {
        User newUser = new User(username, password, User.Role.USER);

        userManager.getUserDAO().addUserToDB(newUser);
        UserManager.currentLoggedInUser = newUser;
    }

    public void handleLogin(User existingUser) {
        UserManager.currentLoggedInUser = existingUser;
    }

    public boolean isPasswordCorrect(String password, User user, UserManager userManager) {
        return userManager.getUserDAO().checkPasswordInDB(password, user.getUsername());
    }
}
