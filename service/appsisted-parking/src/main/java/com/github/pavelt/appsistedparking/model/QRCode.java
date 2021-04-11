package com.github.pavelt.appsistedparking.model;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.shaded.guava.common.annotations.VisibleForTesting;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

public class QRCode {
    public static BufferedImage generateQRCodeImage(String location, String site) throws Exception {

        // fetch the code of the parking location and site from the database
        String code = fetchCode(location, site);
        return generateImage(code);
    }

    /**
     * Package-private method that generates a QR code from text.
     * Needs to be available to package for unit testing.
     * @param code - code to use for generating QR code
     * @return BufferedImage holding the QR Code.
     * @throws Exception
     */
    @VisibleForTesting
    public static BufferedImage generateImage(String code) throws Exception {
        // Create a QR Code
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(code, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * Verifies parking code is correct for a given QR Image
     * @param location - location of the code
     * @param site - site of the code
     * @param qrCode - the full code fetched from the QR Image
     * @return - boolean representing if the code matched what is in the database.
     */
    public static boolean verifyParkingCode(String location, String site, String qrCode) {
        // fetch the code and state if it matches.
        String code = fetchCode(location, site);
        return qrCode.equals(code);
    }

    /**
     * Fetches the uuid code currently representing the parking lot
     * @param location - the location of the parking lot
     * @param site - the parking lot site
     * @return A UUID in string format representing the parking lot
     */
    private static String fetchCode(String location, String site) {
        // fetch the code from the database
        String query = "SELECT code FROM appsisted.parkingsite WHERE location=? AND site=?";

        // bind the location and site to the query
        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(location, site);

        // fetch the result and verify that only 1 item was found.
        ResultSet rs = CassandraClient.getClient().execute(bs);
        List<Row> all = rs.all();
        if (all.size() != 1) {
            throw new RuntimeException("Unexpected number of results for code of parking location -- " +
                    "Should be exactly 1 but was " + all.size());
        }

        // exactly one result was identified as expected, proceed to generate the image
        return all.get(0).getString("code");
    }

    /**
     * Rotates a QR Code and image. Occurs every time someone successfully accesses a parking site.
     * @param location - location of the site rotating a code
     * @param site - the site where the code needs to be rotated
     */
    public static void rotateCode(String location, String site) {
        String query = "UPDATE appsisted.parkingsite SET code=? WHERE location=? AND site=?";

        String newCode = location + "+" + site + "+" + UUID.randomUUID().toString();

        // bind the query
        PreparedStatement ps = CassandraClient.getClient().prepare(query);
        BoundStatement bs = ps.bind(newCode, location, site);

        // run the query
        CassandraClient.getClient().execute(bs);
    }
}
