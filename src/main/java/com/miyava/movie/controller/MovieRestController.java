package com.miyava.movie.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.movie.controller.MovieController;
import com.miyava.movie.controller.MovieRestController;
import com.miyava.movie.model.Movie;
import com.miyava.movie.service.MovieDao;

@RestController
@RequestMapping( MovieRestController.BASE_URL )
public class MovieRestController {

    protected final static String BASE_URL = MovieController.BASE_URL + "/data";

    @Autowired
    private MovieDao movieDao;

    @JsonView( DataTablesOutput.View.class )
    @RequestMapping( value = "/movies", method = RequestMethod.GET )
    public DataTablesOutput<Movie> getMovie( @Valid DataTablesInput input ) {
        return movieDao.findAll( input );
    }

}
