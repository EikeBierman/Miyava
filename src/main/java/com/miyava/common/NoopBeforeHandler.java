package com.miyava.common;

import org.springframework.validation.Errors;

/**
 * BeforeHandler that does nothing.
 */
public class NoopBeforeHandler<EntityType>
    implements BeforeHandler<EntityType> {

    public void beforeSave( EntityType entity, Errors errors ) {
        // do nothing
    }

    public void beforeUpdate( EntityType entity, Errors errors ) {
        // do nothing
    }

    public void beforeSaveOrUpdate( EntityType entity, Errors errors ) {
        // do nothing
    }

    public void beforeDelete( EntityType entity, Errors errors ) {
        return;
    }
}
