package com.miyava.common;

import org.springframework.stereotype.Component;

@Component
public class EntityIdentityMapper<EntityType>
    implements EntityToDTOMapper<EntityType, EntityType> {

    @Override
    public EntityType apply( EntityType input ) {
        return input;
    }

}
