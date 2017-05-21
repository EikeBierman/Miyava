package com.miyava.genres.dto;

import com.miyava.common.EntityToDTOMapper;
import com.miyava.genres.model.Genres;

public class GenresDTOMapper
    implements EntityToDTOMapper<Genres, GenresDTO> {

    @Override
    public GenresDTO apply( Genres input ) {
        GenresDTO dto = new GenresDTO();
        dto.setId( input.getId() );
        dto.setName( input.getName() );

        return dto;
    }

}
