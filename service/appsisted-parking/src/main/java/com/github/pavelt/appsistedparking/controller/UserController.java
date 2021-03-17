package com.github.pavelt.appsistedparking.controller;

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

    @RequestMapping(value = "/user/login", produces = ("application/json"), method = RequestMethod.GET)
    // @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public User userLogin(@RequestParam String username, @RequestParam String password){

        // a secure way to say no user returned. send back a blank one
        User emptyUser = new User("", "", "", "none", "none");

        String sanitizedPassword = Sanitizer.sanitizeAll(password);
        String sanitizedUsername = Sanitizer.sanitizeAll(username);

        if (!User.userExists(username)) {
            return emptyUser;
        }

        User user = User.getUser(sanitizedUsername);
        // verify password
        Boolean verified = PasswordManager.getInstance().verifyPassword(user.getPassword(), user.getSalt(), sanitizedPassword);
        if (verified) {
            return user;
        } else {
            return emptyUser;
        }
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

    @RequestMapping(value = "/user/settings/update", method = RequestMethod.PUT)
    @ResponseBody
    public String updateSettings(@RequestParam String username, @RequestParam String location, @RequestParam String site) {
        if (User.updateSettings(Sanitizer.sanitizeAll(username), Sanitizer.sanitizeAll(location), Sanitizer.sanitizeAll(site))) {
            return "true";
        }

        return "false";
    }
}
