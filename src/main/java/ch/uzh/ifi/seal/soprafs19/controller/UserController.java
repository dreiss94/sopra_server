package ch.uzh.ifi.seal.soprafs19.controller;


import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserExistingException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserNotFoundException;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all(@RequestParam String token) {
        User user = service.getUserByToken(token);
        if (user != null) {
            return service.getUsers();
        }
        throw new AuthenticationException("invalid token "+ token);
    }

    @GetMapping("/users/{username}/login")
    User login(@PathVariable String username, @RequestParam String pw) {
        User user = service.getUser(username);
        if (user != null && user.getPassword().equals(pw)) {
            service.loginUser(user);
            return user;
        } throw new AuthenticationException("wrong password for user " + username);
    }

    @PostMapping("/users")
    ResponseEntity<User> createUser(@RequestBody User newUser) {
        User user = service.getUser(newUser.getUsername());
        if (user != null){
            throw new UserExistingException("add User failed because username already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.createUser(newUser));
    }

    @GetMapping("/users/{id}/logout")
    void logout(@PathVariable Long id, @RequestParam String token) {
        User user = service.getUserById(id);
        if (user != null && user.getToken().equals(token)) {
            service.logoutUser(user);
            return;
        }
        throw new AuthenticationException("wrong token for userId " + id);
    }

    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id, @RequestParam String token) {
        User user = service.getUserByToken(token);
        if (user != null) {
            User other = service.getUserById(id);
            if (other == null) {
                throw new UserNotFoundException("user with userId: " + id + " was not found");
            }
            return other;
        }
        throw new AuthenticationException("invalid token " + token);
    }
    /*
    @CrossOrigin
    @PutMapping("/users/{id}")
    RequestEntity updateUser(@RequestBody User newUser, @PathVariable long id, @RequestParam String token) {
        User user = service.getUserById(id);
        User tokenCheck = service.getUserByToken(token);
        if (user == null){
            throw new UserNotFoundException("user with id: " + " was not found");
        } else if (!user.getToken().equals(tokenCheck.getToken())) {
            throw new AuthenticationException("invalid token " + token);
        }
        this.service.updateUser(user, newUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }*/
}

