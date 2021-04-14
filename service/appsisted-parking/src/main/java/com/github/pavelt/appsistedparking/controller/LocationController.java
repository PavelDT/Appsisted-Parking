package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.model.ParkingSite;
import com.github.pavelt.appsistedparking.security.Sanitizer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController {

    /**
     * /location/status endpoint - responsible for reporting status of parking sites at a specified location and
     * making the recommendation for where the user should park
     * @param location - user's preferred location
     * @param site - user's preffered site
     * @param username - username of user requesting status of parking location
     * @return
     */
    @RequestMapping(value = "/location/status", method = RequestMethod.GET)
    @ResponseBody
    public String getStatus(@RequestParam String location, @RequestParam String site, @RequestParam String username){
        return ParkingSite.getLocationInfo(Sanitizer.sanitizeAll(location), Sanitizer.sanitizeAll(site), Sanitizer.sanitizeAll(username));
    }

    /**
     * /location endpoint - ists all locations
     * @return List of Strings representing all locations
     */
    @RequestMapping(value = "/location/", produces={"application/json"}, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getLocations(){
        return ParkingSite.getLocations();
    }

    /**
     * /location/site endpoint - Lists all sites for a specified location
     * @param location - location on sites to be listed.
     * @return
     */
    @RequestMapping(value = "/location/site", produces={"application/json"}, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getSites(@RequestParam String location){
        return ParkingSite.getSites(Sanitizer.sanitizeAll(location));
    }
}
