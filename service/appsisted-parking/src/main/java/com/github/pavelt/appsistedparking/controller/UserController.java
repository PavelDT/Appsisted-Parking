package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.github.pavelt.appsistedparking.model.User;
import com.github.pavelt.appsistedparking.security.PasswordManager;
import com.github.pavelt.appsistedparking.security.Sanitizer;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @RequestMapping(value = "/user/register", method = RequestMethod.GET)
    // @RequestMapping(value = "/user/register", method = RequestMethod.PUT)
    @ResponseBody
    public String userRegister(@RequestParam String username, @RequestParam String password){

        String sanitizedUsername = Sanitizer.sanitizeAll(username);
        String sanitizedPassword = Sanitizer.sanitizeAll(password);

        // register the user
        return User.register(sanitizedUsername, sanitizedPassword);
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    // @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public String userLogin(@RequestParam String username, @RequestParam String password){

        // todo -- load user status from the user table
        //         and return it as part of the login process

        String sanitizedPassword = Sanitizer.sanitizeAll(password);
        String sanitizedUsername = Sanitizer.sanitizeAll(username);

        if (!User.userExists(username)) {
            return "user does not exist";
        }

        User user = User.getUser(sanitizedUsername);
        // verify password
        Boolean verified = PasswordManager.getInstance().verifyPassword(user.getPassword(), user.getSalt(), sanitizedPassword);

        return verified.toString();
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    // @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    @ResponseBody
    public String userLogout(){
        return "logout";
    }

    @RequestMapping(value = "/user/exists", method = RequestMethod.GET)
    @ResponseBody
    public String userExists(@RequestParam String username){
        String sanitizedUsername = Sanitizer.sanitizeAll(username);
        if (User.userExists(sanitizedUsername)) {
            return "true";
        }
        return "false - " + username;
    }
}
