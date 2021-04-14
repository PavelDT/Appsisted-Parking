package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.model.User;
import com.github.pavelt.appsistedparking.security.PasswordManager;
import com.github.pavelt.appsistedparking.security.Sanitizer;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    /**
     * /user/regiester endpoint - allows a user to register for appsisted-parking
     * @param username - username of user
     * @param password - user's password
     * @return String representing message of success or failure to register
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.GET)
    @ResponseBody
    public String userRegister(@RequestParam String username, @RequestParam String password){

        String sanitizedUsername = Sanitizer.sanitizeAll(username);
        String sanitizedPassword = Sanitizer.sanitizeAll(password);

        // register the user
        return User.register(sanitizedUsername, sanitizedPassword);
    }

    /**
     * /user/login endpoint - allows user to login to the system
     * @param username - username of user
     * @param password - password of user
     * @return - User object as JSON
     */
    @RequestMapping(value = "/user/login", produces = ("application/json"), method = RequestMethod.GET)
    @ResponseBody
    public User userLogin(@RequestParam String username, @RequestParam String password){

        // a secure way to say no user returned. send back a blank one
        User emptyUser = new User("", "", "", "none", "none", 0f);

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

    /**
     * Enables user to logout
     * @param username - username of user to logout.
     * @return - String
     */
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    @ResponseBody
    public String userLogout(@RequestParam String username){
        return "logout";
    }

    /**
     * /user/exists endpoint - Checks if a user exists
     * @param username - username of user
     * @return String representing if a user exists or not.
     */
    @RequestMapping(value = "/user/exists", method = RequestMethod.GET)
    @ResponseBody
    public String userExists(@RequestParam String username){
        String sanitizedUsername = Sanitizer.sanitizeAll(username);
        if (User.userExists(sanitizedUsername)) {
            return "true";
        }
        return "false - " + username;
    }

    /**
     * /user/settings/update endpoint - enables user to update their parking preferences
     * @param username - username of user
     * @param location - location to be set as preffered
     * @param site - site to be set as preffered.
     * @return - String representing success or failure.
     */
    @RequestMapping(value = "/user/settings/update", method = RequestMethod.PUT)
    @ResponseBody
    public String updateSettings(@RequestParam String username, @RequestParam String location, @RequestParam String site) {
        if (User.updateSettings(Sanitizer.sanitizeAll(username), Sanitizer.sanitizeAll(location), Sanitizer.sanitizeAll(site))) {
            return "true";
        }

        return "false";
    }
}
