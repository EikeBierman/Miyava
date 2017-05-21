package com.miyava.common;

import org.springframework.validation.Errors;

import com.google.common.base.Optional;

public interface AfterHandler<EntityType> {

    /**
     * Aktionen, die <b>nach</b> dem eigentlichen Save ausgeführt werden sollen. Wird <b>nicht</b> aufgerufen, wenn die
     * Validierung fehlschlug.
     *
     * @param entity Die gespeicherte Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void afterSave( EntityType entity, Errors errors );

    /**
     * Aktionen, die <b>nach</b> dem eigentlichen Update ausgeführt werden sollen. Wird <b>nicht</b> aufgerufen, wenn
     * die Validierung fehlschlug.
     *
     * @param oldEntity Die alte Entität.
     * @param newEntity Die neue (gespeicherte) Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void afterUpdate( EntityType oldEntity, EntityType newEntity, Errors errors );

    /**
     * Aktionen, die <b>nach</b> dem eigentlichen Save / Update ausgeführt werden sollen. Wird <b>nicht</b> aufgerufen,
     * wenn die Validierung fehlschlug.
     *
     * @param oldEntity Die alte Entität, oder {@link Optional#absent()}.
     * @param entity Die gespeicherte Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void afterSaveOrUpdate( Optional<EntityType> oldEntity, EntityType entity, Errors errors );

    /**
     * Aktionen, die <b>nach</b> dem eigentlichen Delete ausgeführt werden sollen. Wird <b>nicht</b> aufgerufen, wenn
     * die Validierung fehlschlug.
     *
     * @param entity Die gelöschte Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void afterDelete( EntityType entity, Errors errors );
}
