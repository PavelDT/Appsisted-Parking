package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.model.ParkingSite;
import com.github.pavelt.appsistedparking.security.Sanitizer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class LocationController {

    @RequestMapping(value = "/location/status", method = RequestMethod.GET)
    @ResponseBody
    public String getStatus(@RequestParam String location){

        // todo -- maybe update user state from not_parked to PARKING
        return ParkingSite.getLocationInfo(Sanitizer.sanitizeAll(location));
    }

    @RequestMapping(value = "/location/", produces={"application/json"}, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getLocations(){
        return ParkingSite.getLocations();
    }

    @RequestMapping(value = "/location/site", produces={"application/json"}, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getSites(@RequestParam String location){
        return ParkingSite.getSites(Sanitizer.sanitizeAll(location));
    }
}
