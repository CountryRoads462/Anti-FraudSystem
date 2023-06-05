package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.LinkedHashMap;

@RestController
public class RegistrationController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/api/auth/user")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkedHashMap<String, Object> register(@Valid @RequestBody User user) {

        if (userRepo.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        user.setPassword(encoder.encode(user.getPassword()));

        if (userRepo.count() == 0) {
            user.setRole("ROLE_ADMINISTRATOR");
            user.setStatus("UNLOCK");
        }

        userRepo.save(user);

        LinkedHashMap<String, Object> userInfoResponse = new LinkedHashMap<>();

        userInfoResponse.put("id", user.getId());
        userInfoResponse.put("name", user.getName());
        userInfoResponse.put("username", user.getUsername());
        userInfoResponse.put("role", user.getRole().substring(5));

        return userInfoResponse;
    }
}