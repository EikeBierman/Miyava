package com.miyava.genres.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.genres.controller.GenresController;
import com.miyava.genres.controller.GenresRestController;
import com.miyava.genres.model.Genres;
import com.miyava.genres.service.GenresDao;

@RestController
@RequestMapping( GenresRestController.BASE_URL )
public class GenresRestController {

    protected final static String BASE_URL = GenresController.BASE_URL + "/data";

    @Autowired
    private GenresDao genresDao;

    @JsonView( DataTablesOutput.View.class )
    @RequestMapping( value = "/genres", method = RequestMethod.GET )
    public DataTablesOutput<Genres> getGenres( @Valid DataTablesInput input ) {
        return genresDao.findAll( input );
    }

}
