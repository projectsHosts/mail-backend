package mail.com.loginDetails.service;

import mail.com.common.AppConst;
import mail.com.loginDetails.model.User;
import mail.com.loginDetails.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public User signup(User user) {
        // Email already exist?
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException(AppConst.ALREADY_REGISTERED);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // user not found
        if (optionalUser.isEmpty()) {
            throw new RuntimeException(AppConst.USER_NOT_FOUND);
        }
            User user = optionalUser.get();
            // Password match check
            if (!encoder.matches(password, user.getPassword())) {
                throw new RuntimeException(AppConst.INVALID_CREDENTIALS);
            }
        return user;
    }
}
