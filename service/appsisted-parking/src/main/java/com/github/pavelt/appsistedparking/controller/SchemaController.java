package com.github.pavelt.appsistedparking.controller;

import com.github.pavelt.appsistedparking.database.Schema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchemaController {

    /**
     * /schema/createall endpoint creates all the schema needed by Cassandra.
     * @return
     */
    @RequestMapping(value = "/schema/createall", method = RequestMethod.GET)
    @ResponseBody
    public String createAll(){
        Schema schema = new Schema();
        List<String> createdTables = schema.createAllSchema();

        return "Created tables: " + createdTables;
    }
}
