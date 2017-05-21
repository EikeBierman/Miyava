package com.miyava.genres.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.miyava.genres.model.Genres;

public interface GenresRepository
    extends DataTablesRepository<Genres, Long> {

    Genres findOneByName( String name );
}
