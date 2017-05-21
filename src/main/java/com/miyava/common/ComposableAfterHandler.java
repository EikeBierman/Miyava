package com.miyava.common;

import java.util.Arrays;
import java.util.List;

import org.springframework.validation.Errors;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A special {@code AfterHandler} that takes a list of handlers and executes each of them in the after* methods.
 */
public abstract class ComposableAfterHandler<EntityType>
    implements AfterHandler<EntityType> {

    private List<AfterHandler<EntityType>> handlers;

    @SafeVarargs
    public ComposableAfterHandler( AfterHandler<EntityType>... handlers ) {
        Preconditions.checkNotNull( handlers );

        // remove possible null references (i.e. handlers that are only active for a given profile)
        List<AfterHandler<EntityType>> handlerList = Arrays.asList( handlers );
        Iterable<AfterHandler<EntityType>> iterable =
            Iterables.filter( handlerList, Predicates.<AfterHandler<EntityType>> notNull() );
        this.handlers = Lists.newArrayList( iterable );
    }

    @Override
    public void afterSave( EntityType entity, Errors errors ) {
        for ( AfterHandler<EntityType> handler : handlers ) {
            handler.afterSave( entity, errors );

            if ( errors.hasErrors() ) {
                return;
            }
        }
    }

    @Override
    public void afterUpdate( EntityType oldEntity, EntityType newEntity, Errors errors ) {
        for ( AfterHandler<EntityType> handler : handlers ) {
            handler.afterUpdate( oldEntity, newEntity, errors );

            if ( errors.hasErrors() ) {
                return;
            }
        }
    }

    @Override
    public void afterSaveOrUpdate( Optional<EntityType> oldEntity, EntityType entity, Errors errors ) {
        for ( AfterHandler<EntityType> handler : handlers ) {
            handler.afterSaveOrUpdate( oldEntity, entity, errors );

            if ( errors.hasErrors() ) {
                return;
            }
        }
    }

    @Override
    public void afterDelete( EntityType entity, Errors errors ) {
        for ( AfterHandler<EntityType> handler : handlers ) {
            handler.afterDelete( entity, errors );

            if ( errors.hasErrors() ) {
                return;
            }
        }
    }
}
