package com.miyava.common;

import org.springframework.validation.Errors;

public interface BeforeHandler<EntityType> {

    /**
     * Aktionen, die <b>vor</b> dem eigentlichen Save ausgeführt werden sollen.
     *
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void beforeSave( EntityType entity, Errors errors );

    /**
     * Aktionen, die <b>vor</b> dem eigentlichen Update ausgeführt werden sollen.
     *
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void beforeUpdate( EntityType entity, Errors errors );

    /**
     * Aktionen, die <b>vor</b> dem eigentlichen Save / Update ausgeführt werden sollen.
     *
     * @param entity Die zu speichernde Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void beforeSaveOrUpdate( EntityType entity, Errors errors );

    /**
     * Aktionen, die <b>vor</b> dem eigentlichen Delete ausgeführt werden sollen.
     *
     * @param entity Die zu löschende Entität.
     * @param errors Fehlerliste die bei auftretenden Fehlern aktualisiert wird.
     */
    public void beforeDelete( EntityType entity, Errors errors );
}
