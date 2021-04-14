package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.model.ParkingSite;
import com.github.pavelt.appsistedparking.model.QRCode;
import com.github.pavelt.appsistedparking.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;

@RestController
public class ParkingController {

    /**
     * /parking/park endpoint - enables a user to park
     * @param qrCode - qrcode scanned from entrance terminal (contains site and location info)
     * @param username - username of user parking
     * @return - String representing success or failure of parking
     */
    @RequestMapping(value = "/parking/park", method = RequestMethod.POST)
    @ResponseBody
    public String park(@RequestParam String qrCode, @RequestParam String username) {

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

            // now charge the user for parking
            User.chargeUserForParking(username, location, parkingSite);

            return "true";
        }

        // failed to park as codes didn't match
        return "false";
    }

    /**
     * /parking/exit - records a user exiting the parking site
     * @param location - location of the site
     * @param site - the site being exited
     * @return - String representing success or failure of exiting the parking site
     */
    @RequestMapping(value = "/parking/exit", method = RequestMethod.GET)
    @ResponseBody
    public String exitParkinglot(@RequestParam String location, @RequestParam String site) {

        if (ParkingSite.modifyAvailable(location, site, true)) {
            return "true";
        }
        
        return  "false";
    }

    /**
     * /parking/qrcode - Displayes QR Code generated from the parkig site's code stored in Cassandra
     * @param location - the location for which the qr code should be generated
     * @param site - the site for which the qr code should be generated
     * @return - An image visualising the QR Code
     * @throws Exception
     */
    @RequestMapping(value = "/parking/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage displayLocationQRCode(@RequestParam String location, @RequestParam String site) throws Exception {
        return QRCode.generateQRCodeImage(location, site);
    }

    /**
     * Allows the image generated to be converted to an image which is displayable on a web-page.
     */
    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
