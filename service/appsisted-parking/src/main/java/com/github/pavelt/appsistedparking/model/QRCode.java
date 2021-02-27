package com.github.pavelt.appsistedparking.model;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.github.pavelt.appsistedparking.database.CassandraClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.util.List;

public class QRCode {
    public static BufferedImage generateQRCodeImage(String location, String site) throws Exception {

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
        String code = all.get(0).getString("code");

        // Create a QR Code
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(code, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
