package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.model.ParkingSite;
import org.springframework.web.bind.annotation.*;

@RestController
public class ParkingController {

    @RequestMapping(value = "/parking/locationstatus", method = RequestMethod.GET)
    @ResponseBody
    public String locationStatus(@RequestParam String location){

        // todo -- maybe update user state from not_parked to PARKING
        return ParkingSite.getLocationInfo(location);
    }

    @RequestMapping(value = "/parking/park", method = RequestMethod.GET)
    @ResponseBody
    public String park(@RequestParam String location, @RequestParam String parkingSite) {
        return "";
    }
}
