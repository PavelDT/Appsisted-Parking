package com.github.pavelt.appsistedparking.unit.model;

import com.github.pavelt.appsistedparking.model.QRCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

/**
 * Tests everything in QR code that doesn't access database.
 */
@RunWith(SpringRunner.class)
public class QRCodeTest {

    @Test
    public void testQRGeneration() throws Exception {
        BufferedImage img = QRCode.generateImage("stirling Cottrell");

        System.out.println(img.toString());
        assertEquals(200, img.getWidth());
        assertEquals(200, img.getHeight());
    }
}
