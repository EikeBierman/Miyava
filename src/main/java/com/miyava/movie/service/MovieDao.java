package com.miyava.movie.service;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.miyava.common.CrudDao;
import com.miyava.genres.model.Genres;
import com.miyava.movie.model.Movie;
import com.miyava.movie.repository.MovieRepository;

@Service
public class MovieDao
    extends CrudDao<Movie, Long, MovieRepository> {

    @Autowired
    public MovieDao( MovieRepository repository ) {
        super( repository );
    }

    @Override
    public boolean validateOnSaveOrUpdate( Optional<Movie> oldEntity, Movie entity, Errors errors ) {
        if ( entity == null ) {
            return false;
        }

        if ( Strings.isNullOrEmpty( entity.getTitle() ) ) {
            errors.rejectValue( "movie", "movie.messages.movie_empty" );
            return false;
        }

        // Movie bereits vergeben?
        Movie tmpMovie = findOneByTitle( entity.getTitle() );
        if ( tmpMovie != null && !tmpMovie.getId().equals( entity.getId() ) ) {
            //errors.rejectValue( "movie", "movie.messages.id_already_exists", new Object[] { entity.getTitle() }, null );
            return false;
        }

        return true;
    }

    @Override
    public boolean validateOnDelete( Movie entity, Errors errors ) {
        if ( entity != null ) {
            return true;
        }
        return false;
    }

    public Movie findOneByTitle( String title ) {
        return repository.findOneByTitle( title );
    }

    public Iterable<Movie> findAll() {
        return repository.findAll();
    }

    public ArrayList<Movie> getMovies() {
        Iterable<Movie> source = repository.findAll();
        ArrayList<Movie> target = new ArrayList<Movie>();
        source.forEach( target::add );
        return target;
    }

}
