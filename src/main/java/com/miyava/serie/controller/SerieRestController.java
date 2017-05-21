package com.miyava.serie.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.serie.controller.SerieController;
import com.miyava.serie.controller.SerieRestController;
import com.miyava.serie.model.Serie;
import com.miyava.serie.service.SerieDao;

@RestController
@RequestMapping( SerieRestController.BASE_URL )
public class SerieRestController {

    protected final static String BASE_URL = SerieController.BASE_URL + "/data";

    @Autowired
    private SerieDao serieDao;

    @JsonView( DataTablesOutput.View.class )
    @RequestMapping( value = "/series", method = RequestMethod.GET )
    public DataTablesOutput<Serie> getMovie( @Valid DataTablesInput input ) {
        return serieDao.findAll( input );
    }

}
