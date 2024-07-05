package user;

import database.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shared.ResponseMessage;
import user.credential.User;
import user.manager.UserManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMangerTest {
    UserManager userManager;
    User user;
    UserDAO mockUserDAO;
    String username = "exampleUsername";
    String password = "examplePassword";

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        user = new User(username, password, User.Role.USER);
        mockUserDAO = mock(UserDAO.class);
        userManager.setUserDAO(mockUserDAO);
    }

    @Test
    @DisplayName("Should test user registration with existing user in database")
    void testRegisterAndGetResponse() {
        when(mockUserDAO.getUserFromDB(username)).thenReturn(null);

        String response = userManager.registerAndGetResponse(username, password);

        assertEquals(ResponseMessage.REGISTRATION_SUCCESSFUL.getResponse(), response);
    }

    @Test
    @DisplayName("Should test user registration with non-existent user in database")
    void testRegisterAndGetResponse_UserExists() {
        when(mockUserDAO.getUserFromDB(username)).thenReturn(user);

        String response = userManager.registerAndGetResponse(username, password);

        assertEquals(ResponseMessage.REGISTRATION_FAILED_USER_EXISTS.getResponse(), response);
    }

    @Test
    @DisplayName("Should test user login with existing user in database")
    void testLoginAndGetResponse() {
        when(mockUserDAO.getUserFromDB(username)).thenReturn(user);
        when(mockUserDAO.checkPasswordInDB(password, username)).thenReturn(true);

        String response = userManager.loginAndGetResponse(username, password);

        assertEquals(ResponseMessage.USER_LOGIN_SUCCEEDED.getResponse(), response);
        assertEquals(user, UserManager.currentLoggedInUser);
    }

    @Test
    @DisplayName("Should test user login with non-existent user in database")
    void testLoginAndGetResponse_IncorrectPassword() {
        when(mockUserDAO.getUserFromDB(username)).thenReturn(user);
        when(mockUserDAO.checkPasswordInDB(username,password)).thenReturn(false);

        String response = userManager.loginAndGetResponse(username, password);

        assertEquals(ResponseMessage.LOGIN_FAILED_INCORRECT_PASSWORD.getResponse(), response);
        assertNull(UserManager.currentLoggedInUser);
    }

    @Test
    @DisplayName("Should test user registration for the first time")
    void testGetUserByUsername() {
        when(mockUserDAO.getUserFromDB("exampleUser")).thenReturn(user);

        User foundUser = userManager.getUserByUsername("exampleUser");
        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("Should test user registration for the first time")
    void testPasswordChange() {
        String newPassword = "newPassword";
        when(mockUserDAO.getUserFromDB(password)).thenReturn(user);

        userManager.changePassword(user, newPassword);

        assertNotEquals(password, user.getPassword());
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    @DisplayName("Should test user deletion")
    void testDeleteUser() {
        doNothing().when(mockUserDAO).deleteUserFromDB(username);

        userManager.deleteUser(user);

        User foundUser = userManager.getUserByUsername(username);
        assertNull(foundUser);
    }

    @Test
    @DisplayName("Should test user switch by admin")
    void testSwitchUser() {
        userManager.switchUser(user);

        assertEquals(user, UserManager.currentLoggedInUser);
        assertTrue(UserManager.ifAdminSwitched);
    }

    @Test
    @DisplayName("Should test user role change")
    void testChangeRole() {
        when(mockUserDAO.getUserFromDB(username)).thenReturn(user);

        userManager.changeUserRole(user, User.Role.ADMIN);

        assertEquals(User.Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should test user switch by admin")
    void testLogoutAndGetResponse(){
        String response = userManager.logoutAndGetResponse();

        assertNull(UserManager.currentLoggedInUser);
        assertFalse(UserManager.ifAdminSwitched);
        assertEquals(ResponseMessage.LOGOUT_SUCCEEDED.getResponse(), response);
    }
}