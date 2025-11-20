package mail.com.loginDetails.service;

import mail.com.loginDetails.model.User;

public interface UserService {
    public User signup(User user);
    public User login(String email, String password);
}
