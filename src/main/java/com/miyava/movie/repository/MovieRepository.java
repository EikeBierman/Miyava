package com.miyava.movie.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.miyava.movie.model.Movie;

public interface MovieRepository
    extends DataTablesRepository<Movie, Long> {

    Movie findOneByTitle( String title );
}
