package com.miyava.common;

import org.springframework.validation.Errors;

import com.google.common.base.Optional;

public class NoopAfterHandler<EntityType>
    implements AfterHandler<EntityType> {

    @Override
    public void afterSave( EntityType entity, Errors errors ) {
        return;
    }

    @Override
    public void afterUpdate( EntityType oldEntity, EntityType newEntity, Errors errors ) {
        return;
    }

    @Override
    public void afterSaveOrUpdate( Optional<EntityType> oldEntity, EntityType entity, Errors errors ) {
        return;
    }

    @Override
    public void afterDelete( EntityType entity, Errors errors ) {
        return;
    }
}
