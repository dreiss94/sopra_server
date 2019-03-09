package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserExistingException;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Date;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @GetMapping("/users/{username}")
    User login(@PathVariable String username, @RequestParam String pw) {
        User user = service.getUser(username);
        if (user != null && user.getPassword().equals(pw)) {
            return user;
        } else throw new AuthenticationException("wrong password for user " + username);
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        System.out.print("here");
        User user = service.getUser(newUser.getUsername());
        if (user != null){
            throw new UserExistingException("add User failed because username already exists");
        }
        return this.service.createUser(newUser);
    }
/*
    @PostMapping("/users/login")
    AuthorizationCredentials login(@RequestBody LoginCredentials cred) {
        AuthorizationCredentials acred = new AuthorizationCredentials();
        acred.token = this.service.loginUser(cred.username, cred.password);
        return acred;
    }
    @PostMapping("/users/logout")
    @ResponseStatus(HttpStatus.OK)
    String logout(@RequestBody AuthorizationCredentials cred) {
        return this.service.logoutUser(cred.token);
    }*/
}
class AuthorizationCredentials implements Serializable {
    public String token;
}

class LoginCredentials implements Serializable {
    public String username;
    public String password;
}

