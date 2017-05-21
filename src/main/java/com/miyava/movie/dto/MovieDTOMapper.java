package com.miyava.movie.dto;

import com.miyava.common.EntityToDTOMapper;
import com.miyava.movie.model.Movie;

public class MovieDTOMapper
    implements EntityToDTOMapper<Movie, MovieDTO> {

    @Override
    public MovieDTO apply( Movie input ) {
        MovieDTO dto = new MovieDTO();
        dto.setId( input.getId() );
        dto.setName( input.getTitle() );
        dto.setDescription( input.getOverview());

        return dto;
    }

}
