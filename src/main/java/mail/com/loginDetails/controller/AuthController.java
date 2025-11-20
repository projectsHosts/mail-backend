package mail.com.loginDetails.controller;


import mail.com.loginDetails.model.User;
import mail.com.loginDetails.security.JwtUtil;
import mail.com.loginDetails.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            userService.signup(user);

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Signup Successful");

            return ResponseEntity.ok(body);
        } catch (RuntimeException e) {
            // yaha tum AppConst ka message bhi use kar sakta hai
            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(body);
        }
    }

    @PostMapping("/login")
    public Object login(@RequestBody User user) {
        User loggedUser = userService.login(user.getEmail(), user.getPassword());
        if(loggedUser != null) {
            String token = jwtUtil.generateToken(loggedUser.getEmail());
            return new Object() {
                public final String name = loggedUser.getName();
                public final String email = loggedUser.getEmail();
                public final String jwt = token;
            };
        }
        return "Invalid Credentials";
    }

}
