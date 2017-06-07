package com.miyava.serie.dto;

import com.miyava.common.EntityToDTOMapper;
import com.miyava.serie.model.Serie;

public class SerieDTOMapper
    implements EntityToDTOMapper<Serie, SerieDTO> {

    @Override
    public SerieDTO apply( Serie input ) {
        SerieDTO dto = new SerieDTO();
        dto.setId( input.getId() );
        dto.setName( input.getName() );
        /* dto.setDescription( input.getOverview() ); */

        return dto;
    }

}
