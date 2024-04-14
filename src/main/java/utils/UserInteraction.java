package utils;

import mail.Mail;
import user.User;
import user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class UserInteraction {
    private BufferedReader reader;
    public UserInteraction(BufferedReader reader) {
        this.reader = reader;
    }

    public String getUsername() throws IOException {
        System.out.println("Type username:");
        return reader.readLine();
    }

    public String getPassword() throws IOException {
        System.out.println("Type password:");
        return reader.readLine();
    }

    public String getRecipient() throws IOException {
        System.out.println("Type recipient's username:");
        return reader.readLine();
    }

    public String getMessage() throws IOException {
        System.out.println("Type your message:");
        return reader.readLine();
    }

    public String chooseMailBox() throws IOException {
        while (true) {
            System.out.println("Choose mailbox: OPENED / UNREAD / SENT");
            String input = reader.readLine();
            switch (input.toUpperCase()) {
                case "OPENED":
                    return "OPENED";
                case "UNREAD":
                    return "UNREAD";
                case "SENT":
                    return "SENT";
                default:
                    System.out.println("Incorrect input. Please, try again.");
                    break;
            }
        }
    }

}
