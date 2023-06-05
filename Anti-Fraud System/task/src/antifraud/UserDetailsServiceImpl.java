package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + username);
        }

        if (user.getStatus().equals("LOCK")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return new UserDetailsImpl(user);
    }
}