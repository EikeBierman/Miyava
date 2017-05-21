package com.miyava.serie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.auditing.AuditedEntity;
import com.miyava.common.NotEmpty;

@Entity
public class Serie
    extends AuditedEntity
    implements com.miyava.common.Entity<Long> {

    @Column( name = "serie_id" )
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @JsonView( DataTablesOutput.View.class )
    private Long id;

    @NotEmpty( message = "serie.messages.serie_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView( DataTablesOutput.View.class )
    private String name;

    @NotEmpty( message = "serie.messages.description_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String description;

    public Serie() {}

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }
}
