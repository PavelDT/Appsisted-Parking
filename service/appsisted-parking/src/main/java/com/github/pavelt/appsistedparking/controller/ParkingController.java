package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.model.ParkingSite;
import com.github.pavelt.appsistedparking.model.QRCode;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;

@RestController
public class ParkingController {

    @RequestMapping(value = "/parking/park", method = RequestMethod.GET)
    @ResponseBody
    public String park(@RequestParam String qrCode) {

        String data[] = qrCode.split("\\+");
        String location = data[0];
        String parkingSite = data[1];

        // the third component of the split is never used on its own
        // the verification process is interested in the full QR code
        // not just the UUID
        if (QRCode.verifyParkingCode(location, parkingSite, qrCode)) {
            // reduce the space available for that parking lot.
            ParkingSite.modifyAvailable(location, parkingSite, false);
            // rotate the parking code
            QRCode.rotateCode(location, parkingSite);

            return "true";
        }

        // failed to park as codes didn't match
        return "false";
    }

    @RequestMapping(value = "/parking/exit", method = RequestMethod.GET)
    @ResponseBody
    public String exitParkinglot(@RequestParam String location, @RequestParam String site) {

        if (ParkingSite.modifyAvailable(location, site, true)) {
            return "true";
        }
        
        return  "false";
    }

    @RequestMapping(value = "/parking/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage displayLocationQRCode(@RequestParam String location, @RequestParam String site) throws Exception {
        return QRCode.generateQRCodeImage(location, site);
    }

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
