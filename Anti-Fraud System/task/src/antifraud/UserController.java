package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepo;

    @GetMapping(path = "/api/auth/list")
    public List<LinkedHashMap<String, Object>> getUsers() {

        List<LinkedHashMap<String, Object>> userListInfo = new ArrayList<>();

        userRepo.findAll().forEach(user -> {

            LinkedHashMap<String, Object> userDetails = new LinkedHashMap<>();

            userDetails.put("id", user.getId());
            userDetails.put("name", user.getName());
            userDetails.put("username", user.getUsername());
            userDetails.put("role", user.getRole().substring(5));

            userListInfo.add(userDetails);
        });

        return userListInfo;
    }

    @Transactional
    @DeleteMapping(path = "/api/auth/user/{username}")
    public LinkedHashMap<String, String> deleteUser(@PathVariable String username) {

        if (!userRepo.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        userRepo.deleteByUsername(username);

        LinkedHashMap<String, String> deleteResponse = new LinkedHashMap<>();

        deleteResponse.put("username", username);
        deleteResponse.put("status", "Deleted successfully!");

        return deleteResponse;
    }

    @ExceptionHandler(ResponseStatusException.class)
    @PutMapping(path = "/api/auth/role")
    public LinkedHashMap<String, Object> changeRole(@RequestBody ChangeRoleForm changeRoleForm) throws Throwable {

        if (!changeRoleForm.getRole().matches("(" +
                "MERCHANT|" +
                "SUPPORT" +
                ")")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        User user = userRepo.findByUsername(changeRoleForm.getUsername())
                .orElseThrow((Supplier<Throwable>) () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        /*
        if (user.isRoleIsAssigned()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            user.setRoleIsAssigned(true);
        }

         */

        if (user.getRole().equals("ROLE_" + changeRoleForm.getRole())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        String role = "ROLE_" + changeRoleForm.getRole();

        user.setRole(role);
        userRepo.save(user);

        LinkedHashMap<String, Object> changeRoleResponse = new LinkedHashMap<>();

        changeRoleResponse.put("id", user.getId());
        changeRoleResponse.put("name", user.getName());
        changeRoleResponse.put("username", user.getUsername());
        changeRoleResponse.put("role", user.getRole().substring(5));

        return changeRoleResponse;
    }

    @PutMapping(path = "/api/auth/access")
    public LinkedHashMap<String, String> lockUnlockUser(@RequestBody LockUnlockUserForm lockUnlockUserForm) {

        User user = userRepo.findByUsername(lockUnlockUserForm.getUsername()).orElseThrow();

        user.setStatus(lockUnlockUserForm.getOperation());
        userRepo.save(user);

        LinkedHashMap<String, String> lockUnlockUserResponse = new LinkedHashMap<>();

        lockUnlockUserResponse.put("status", String.format("User %s %sed!", user.getUsername(), user.getStatus().toLowerCase(Locale.ROOT)));

        return lockUnlockUserResponse;
    }
}
