package com.miyava.serie.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.miyava.serie.model.Serie;

public interface SerieRepository
    extends DataTablesRepository<Serie, Long> {

    Serie findOneByName( String name );
}
