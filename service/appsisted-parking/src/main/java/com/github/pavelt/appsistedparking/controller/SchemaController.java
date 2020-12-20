package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.database.Schema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchemaController {

    @RequestMapping(value = "/schema/createall", method = RequestMethod.GET)
    // @RequestMapping(value = "/schema/createall", method = RequestMethod.PUT)
    public String createAll(){
        Schema schema = new Schema();
        List<String> createdTables = schema.createAllSchema();

        return "Created tables: " + createdTables;
    }
}
