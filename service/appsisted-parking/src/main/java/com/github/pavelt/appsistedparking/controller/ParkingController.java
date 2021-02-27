package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.model.ParkingSite;
import com.github.pavelt.appsistedparking.model.QRCode;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;

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

//    @GetMapping(value = "/barbecue/ean13/{barcode}", produces = MediaType.IMAGE_PNG_VALUE)
    @RequestMapping(value = "/parking/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage barbecueEAN13Barcode(@RequestParam String location, @RequestParam String site) throws Exception {
        return QRCode.generateQRCodeImage(location, site);
    }

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
