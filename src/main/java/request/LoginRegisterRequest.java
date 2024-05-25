package request;

public class LoginRegisterRequest extends Request {
    public LoginRegisterRequest(String request, String username, String password) {
        super(request);
        this.username = username;
        this.password = password;
    }
}