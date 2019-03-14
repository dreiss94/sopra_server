package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import ch.uzh.ifi.seal.soprafs19.controller.UserController;
import ch.uzh.ifi.seal.soprafs19.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UserNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Iterator;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Test
    public void createUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());

        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.OFFLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }

    @Test
    public void getUsers(){
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        userService.createUser(testUser);

        Iterable<User> users = userService.getUsers();
        Iterator<User> iter = users.iterator();
        Assert.assertEquals(iter.next(), testUser);
    }

    @Test
    public void getUser(){
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        userService.createUser(testUser);
        User createdUser = userService.getUser("testUsername");

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }

    @Test
    public void loginValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        userService.createUser(testUser);
        User createdUser = userService.createUser(testUser);

        userService.loginUser(testUser);
        Assert.assertEquals(createdUser, testUser);
        Assert.assertEquals(createdUser.getStatus(), UserStatus.ONLINE);
        Assert.assertNotNull(createdUser.getToken());
    }

    @Test (expected = AuthenticationException.class)
    public void loginInvalidUsername() {
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        userService.createUser(testUser);
        userController.login("!= testUsername", "testPassword");
    }

    @Test(expected = AuthenticationException.class)
    public void loginInvalidPassword() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        userService.createUser(testUser);
        userController.login("testUsername", "!= testPassword");
    }

    @Test
    public void logoutValidToken(){
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        User createdUser = userService.createUser(testUser);

        userService.loginUser(createdUser);
        userService.logoutUser(createdUser);
        Assert.assertEquals(userRepository.findByUsername("testUsername").getStatus(), UserStatus.OFFLINE);
    }

    @Test(expected = AuthenticationException.class)
    public void logoutInvalidToken() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        User createdUser = userService.createUser(testUser);

        userController.logout(createdUser.getId(), "differentToken");
    }

    @Test
    public void getValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        User createdUser = userService.createUser(testUser);

        Assert.assertEquals(userService.getUserById(createdUser.getId()), createdUser);
    }

    @Test(expected= UserNotFoundException.class)
    public void getInvalidUserId() {
        userRepository.deleteAll();
        Long id = 1L;
        userService.getUserById(id);
    }

    @Test
    public void updateValidUser() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        User createdUser = userService.createUser(testUser);
        userService.loginUser(createdUser);

        User updatedUser = new User();
        updatedUser.setName("testName");
        updatedUser.setUsername("UPDATEDtestUsername");
        updatedUser.setPassword("testPassword");
        updatedUser.setBirthday(new Date());

        userController.updateUser(updatedUser, createdUser.getId(), createdUser.getToken());

        Assert.assertEquals(userService.getUserById(createdUser.getId()).getUsername(), updatedUser.getUsername());
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUSerInvalidId() {
        userRepository.deleteAll();
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setBirthday(new Date());
        User createdUser = userService.createUser(testUser);
        userService.loginUser(createdUser);

        User updatedUser = new User();
        updatedUser.setName("testName");
        updatedUser.setUsername("UPDATEDtestUsername");
        updatedUser.setPassword("testPassword");
        updatedUser.setBirthday(new Date());

        Long id = 123L;

        userController.updateUser(updatedUser, id, createdUser.getToken());

        Assert.assertEquals(userService.getUserById(createdUser.getId()).getUsername(), updatedUser.getUsername());
    }




}
