package com.miyava.auditing;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import com.miyava.common.EntityWithUUID;

@MappedSuperclass
@EntityListeners( AuditingEntityListener.class )
public class AuditedEntityWithUUID
    extends EntityWithUUID {

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    @DateTimeFormat( pattern = "dd.MM.yyyy HH:mm" )
    private Date createdDate;

    @LastModifiedDate
    @DateTimeFormat( pattern = "dd.MM.yyyy HH:mm" )
    private Date lastModifiedDate;

    public AuditedEntityWithUUID() {
        super();
    }

    public AuditedEntityWithUUID( String id ) {
        super( id );
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy( String createdBy ) {
        if ( createdBy != null ) {
            this.createdBy = createdBy;
        }
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy( String lastModifiedBy ) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate( Date createdDate ) {
        if ( createdDate != null ) {
            this.createdDate = createdDate;
        }
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate( Date lastModifiedDate ) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
