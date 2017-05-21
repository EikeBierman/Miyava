package com.miyava.common;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * This DAO is responsible for creating/updating/deleting objects in the responsible repository for the entityType. One
 * can initialize the concrete DAO with custom {@link BeforeHandler before} and {@link AfterHandler after} handler, that
 * get executed before/after the CRUD operation.
 */
public abstract class CrudDao<EntityType extends Entity<ID>, ID extends Serializable, RepositoryType extends DataTablesRepository<EntityType, ID>> {

    private static final Logger LOGGER = LoggerFactory.getLogger( CrudDao.class );

    protected RepositoryType repository;

    @Autowired
    protected EntityManager em;

    private BeforeHandler<EntityType> before;

    private AfterHandler<EntityType> after;

    /**
     * Initializes this DAO with the given {@code beforeHandler} and {@code afterHandler}.
     *
     * @param repository
     * @param before
     * @param after
     */
    public CrudDao( RepositoryType repository,
                    BeforeHandler<EntityType> before, AfterHandler<EntityType> after ) {
        this( repository );
        this.before = Preconditions.checkNotNull( before );
        this.after = Preconditions.checkNotNull( after );
    }

    /**
     * Initializes this DAO with the given {@code beforeHandler}, using the {@link NoopAfterHandler} as
     * {@code afterHandler}.
     *
     * @param repository
     * @param before
     */
    public CrudDao( RepositoryType repository, BeforeHandler<EntityType> before ) {
        this( repository );
        this.before = Preconditions.checkNotNull( before );
        this.after = new NoopAfterHandler<EntityType>();
    }

    /**
     * Initializes this DAO with the given {@code afterHandler}, using the {@link NoopBeforeHandler} as
     * {@code beforeHandler}.
     *
     * @param repository
     * @param before
     */
    public CrudDao( RepositoryType repository, AfterHandler<EntityType> after ) {
        this( repository );
        this.before = new NoopBeforeHandler<EntityType>();
        this.after = Preconditions.checkNotNull( after );
    }

    /**
     * Initializes this DAO using the {@link NoopBeforeHandler} and the {@link NoopAfterHandler}
     *
     * @param repository
     */
    public CrudDao( RepositoryType repository ) {
        this.repository = repository;
        this.before = new NoopBeforeHandler<EntityType>();
        this.after = new NoopAfterHandler<EntityType>();
    }

    public RepositoryType getRepository() {
        return repository;
    }

    /**
     * Eine Entität anlegen.
     * <ul>
     * <li>Führt {@link BeforeHandler#beforeSave(Object, Errors)} VOR dem validieren und save aus.</li>
     * <li>Führt {@link AfterHandler#afterSave(Object, Errors)} nach dem save aus.</li>
     * </ul>
     *
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return Die gespeicherte Entität, oder <i>null<i>, falls Validierungen fehlgeschlagen sind.
     * @see #validateOnSave(Object, Errors)
     */
    @Transactional
    public EntityType doSave( EntityType entity, Errors errors ) {
        String entityTypeName = entity.getClass().getName();

        LOGGER.info( String.format( "Speichere Entität vom Typ [%s]", entityTypeName ) );

        LOGGER.info( String.format( "Führe Vorverarbeitungsschritte für Entität vom Typ [%s] aus", entityTypeName ) );
        before.beforeSave( entity, errors );

        LOGGER.info( String.format( "Führe Validierungsschritte für Entität vom Typ [%s] aus", entityTypeName ) );
        if ( validateOnSave( entity, errors ) ) {
            LOGGER.info( String.format( "Persistiere Entität vom Typ [%s]", entityTypeName ) );
            EntityType newEntity = repository.save( entity );

            LOGGER.info( String.format( "Führe Nachverarbeitungsschritte für Entität vom Typ [%s] aus", entityTypeName ) );
            after.afterSave( newEntity, errors );

            return newEntity;
        }
        else {
            LOGGER.info( String.format( "Validierung für Entität vom Typ [%s] fehlgeschlagen", entityTypeName ) );
            return null;
        }
    }

    /**
     * Eine Entität aktualisiseren.
     * <ul>
     * <li>Führt {@link BeforeHandler#beforeUpdate(Object, Errors)} VOR dem validieren und update aus.</li>
     * <li>Führt {@link AfterHandler#afterUpdate(Object, Errors)} nach dem update aus.</li>
     * </ul>
     *
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return Die gespeicherte Entität, oder <i>null<i>, falls Validierungen fehlgeschlagen sind.
     */
    @Transactional
    public EntityType doUpdate( EntityType entity, Errors errors ) {
        before.beforeUpdate( entity, errors );

        EntityType existingEntity = findOne( entity.getId() );
        em.detach( existingEntity );

        if ( validateOnUpdate( existingEntity, entity, errors ) ) {
            findOne( entity.getId() );
            EntityType newEntity = repository.save( entity );
            after.afterUpdate( existingEntity, newEntity, errors );
            return newEntity;
        }
        else {
            return null;
        }
    }

    /**
     * Eine Entität speichern bzw. aktualisieren
     *
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return Die gespeicherte Entität, oder <i>null<i>, falls Validierungen fehlgeschlagen sind.
     */
    @Transactional
    public EntityType doSaveOrUpdate( EntityType entity, Errors errors ) {
        before.beforeSaveOrUpdate( entity, errors );

        Optional<EntityType> oldEntity = Optional.absent();
        if ( repository.exists( entity.getId() ) ) {
            EntityType existingEntity = findOne( entity.getId() );
            em.detach( existingEntity );
            oldEntity = Optional.of( existingEntity );
        }

        if ( validateOnSaveOrUpdate( oldEntity, entity, errors ) ) {
            if ( oldEntity.isPresent() ) {
                findOne( entity.getId() );
            }
            EntityType newEntity = repository.save( entity );
            after.afterSaveOrUpdate( oldEntity, newEntity, errors );
            return newEntity;
        }
        else {
            return null;
        }
    }

    /**
     * Eine Entität löschen.
     *
     * @param entity Die zu löschende Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return <b>true</b>, falls der Löschvorgang erfolgreich war <br>
     *         <b>false</b>, sonst
     */
    @Transactional
    public boolean doDelete( EntityType entity, Errors errors ) {

        if ( entity == null ) {
            return false;
        }

        before.beforeDelete( entity, errors );

        if ( validateOnDelete( entity, errors ) ) {
            repository.delete( entity );
            after.afterDelete( entity, errors );
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Eine Entität löschen.
     *
     * @param id Die ID der zu löschenden Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return <b>true</b>, falls der Löschvorgang erfolgreich war <br>
     *         <b>false</b>, sonst
     */
    @Transactional
    public boolean doDelete( ID id, Errors errors ) {
        return doDelete( findOne( id ), errors );
    }

    /**
     * Validierungen, die sicherstellen, dass die Entität gespeichert werden darf.
     *
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return <b>true</b>, falls die Entität gespeichert werden darf <br>
     *         <b>false</b>, sonst
     */
    public boolean validateOnSave( EntityType entity, Errors errors ) {
        return validateOnSaveOrUpdate( Optional.<EntityType> absent(), entity, errors );
    }

    /**
     * Validierungen, die sicherstellen, dass die Entität aktualisiert werden darf.
     *
     * @param oldEntity Die alte Entität
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return <b>true</b>, falls die Entität aktualisiert werden darf <br>
     *         <b>false</b>, sonst
     */
    public boolean validateOnUpdate( EntityType oldEntity, EntityType entity, Errors errors ) {
        return validateOnSaveOrUpdate( Optional.of( oldEntity ), entity, errors );
    }

    /**
     * Validierungen, die sicherstellen, dass die Entität gespeichert / aktualisiert werden darf.
     *
     * @param oldEntity Die alte Entität oder {@link Optional#absent()}.
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return <b>true</b>, falls die Entität gespeichert / aktualisiert werden darf <br>
     *         <b>false</b>, sonst
     */
    public abstract boolean validateOnSaveOrUpdate( Optional<EntityType> oldEntity, EntityType entity, Errors errors );

    /**
     * Validierungen, die sicherstellen, dass die Entität gelöscht werden darf.
     *
     * @param entity Die zu löschende Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     * @return <b>true</b>, falls die Entität gelöscht werden darf <br>
     *         <b>false</b>, sonst
     */
    public abstract boolean validateOnDelete( EntityType entity, Errors errors );

    /**
     * Ruft ein {@link JpaRepository#findOne(Serializable)} mit der angegebenen ID
     *
     * @param id
     * @return die Entität oder {@code null}.
     */
    public EntityType findOne( ID id ) {
        return repository.findOne( id );
    }

    public boolean exists( ID id ) {
        return repository.exists( id );
    }

    /**
     * @return the before
     */
    public BeforeHandler<EntityType> getBefore() {
        return before;
    }

    /**
     * @return the after
     */
    public AfterHandler<EntityType> getAfter() {
        return after;
    }

    public DataTablesOutput<EntityType> findAll( DataTablesInput input ) {
        return repository.findAll( input );
    }
}
