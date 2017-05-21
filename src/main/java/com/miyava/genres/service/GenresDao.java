package com.miyava.genres.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.miyava.common.CrudDao;
import com.miyava.genres.model.Genres;
import com.miyava.genres.repository.GenresRepository;

@Service
public class GenresDao
    extends CrudDao<Genres, Long, GenresRepository> {

    @Autowired
    public GenresDao( GenresRepository repository ) {
        super( repository );
    }

    @Override
    public boolean validateOnSaveOrUpdate( Optional<Genres> oldEntity, Genres entity, Errors errors ) {
        if ( entity == null ) {
            return false;
        }
        // Genrefeld ist Leer?
        if ( Strings.isNullOrEmpty( entity.getName() ) ) {
            errors.rejectValue( "genres", "Das Feld darf nicht Leer sein!" );
            return false;
        }

        // Genre bereits vergeben?
        Genres tmpGenres = findOneByName( entity.getName() );
        if ( tmpGenres != null && !tmpGenres.getId().equals( entity.getId() ) ) {
            errors.rejectValue( "genres", "Genre ist Bereits vergeben!", new Object[] { entity.getName() }, null );
            return false;
        }
        return true;
    }

    @Override
    public boolean validateOnDelete( Genres entity, Errors errors ) {
        if ( entity != null ) {
            return true;
        }
        return false;
    }

    public Genres findOneByName( String name ) {
        return repository.findOneByName( name );
    }

    public Iterable<Genres> findAll() {
        return repository.findAll( );
    }

}
