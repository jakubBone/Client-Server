package user;

import mail.MailBox;

public class Admin extends User {

    public Admin(String username, String hashedPassword, Role role) {
        super(username, hashedPassword, role);
    }

}
