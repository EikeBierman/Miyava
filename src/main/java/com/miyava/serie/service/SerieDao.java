package com.miyava.serie.service;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.miyava.common.CrudDao;
import com.miyava.serie.model.Serie;
import com.miyava.serie.repository.SerieRepository;

@Service
public class SerieDao
    extends CrudDao<Serie, Long, SerieRepository> {

    @Autowired
    public SerieDao( SerieRepository repository ) {
        super( repository );
    }

    @Override
    public boolean validateOnSaveOrUpdate( Optional<Serie> oldEntity, Serie entity, Errors errors ) {
        if ( entity == null ) {
            return false;
        }

        if ( Strings.isNullOrEmpty( entity.getName() ) ) {
            errors.rejectValue( "serie", "serie.messages.serie_empty" );
            return false;
        }

        // Serie bereits vergeben?
        Serie tmpSerie = findOneByName( entity.getName() );
        if ( tmpSerie != null && !tmpSerie.getId().equals( entity.getId() ) ) {
            errors.rejectValue( "serie", "serie.messages.id_already_exists", new Object[] { entity.getName() }, null );
            return false;
        }

        return true;
    }

    @Override
    public boolean validateOnDelete( Serie entity, Errors errors ) {
        if ( entity != null ) {
            return true;
        }
        return false;
    }

    public Serie findOneByName( String name ) {
        return repository.findOneByName( name );
    }

    public ArrayList<Serie> getSeries() {
        Iterable<Serie> source = repository.findAll();
        ArrayList<Serie> target = new ArrayList<Serie>();
        source.forEach( target::add );
        return target;
    }

    public Iterable<Serie> findAll() {
        return repository.findAll();
    }
}
