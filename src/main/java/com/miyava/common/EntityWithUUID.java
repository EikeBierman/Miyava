package com.miyava.common;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entities based on this class automatically get a UUID based ID
 */
@MappedSuperclass
public abstract class EntityWithUUID
    implements Entity<String> {

    @Id
    @JsonView( DataTablesOutput.View.class )
    protected String id;

    public EntityWithUUID() {
        super();
        this.id = UUID.randomUUID().toString();
    }

    public EntityWithUUID( String id ) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( obj == null ) {
            return false;
        }

        if ( !( obj instanceof EntityWithUUID ) ) {
            return false;
        }

        EntityWithUUID other = (EntityWithUUID) obj;
        return getId().equals( other.getId() );
    }

}
