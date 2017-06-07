package com.miyava.serie.model;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.auditing.AuditedEntity;
import com.miyava.common.NotEmpty;

@Entity
public class Episode
    extends AuditedEntity
    implements com.miyava.common.Entity<Long> {

    @Column( name = "episode_id" )
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @JsonView( DataTablesOutput.View.class )
    private Long id;

    @Column( unique = false, columnDefinition = "DATETIME", name = "air_date" )
    @JsonView( DataTablesOutput.View.class )
    private Date air_date;

    @Column( name = "episodeNumber" )
    private Long episode_number;

    @Column( nullable = true, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String name;

    @NotEmpty( message = "episode.messages.description_empty" )
    @Column( nullable = true, unique = false )
    @Lob
    @JsonView( DataTablesOutput.View.class )
    private String overview;

    @Column( nullable = true, unique = false )
    @JsonView( DataTablesOutput.View.class )
    private String short_Overview;

    @Column( name = "seasonNumber" )
    private Long season_number;

    @ManyToOne
    @JoinColumn( name = "season_id" )
    private Season season_id;

    public Episode() {}

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public Date getAir_date() {
        return air_date;
    }

    public void setAir_date( Date air_date ) {
        this.air_date = air_date;
    }

    public Long getEpisode_number() {
        return episode_number;
    }

    public void setEpisode_number( Long episode_number ) {
        this.episode_number = episode_number;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview( String overview ) {
        this.overview = overview;
    }

    public String getShort_Overview() {
        return short_Overview;
    }

    public void setShort_Overview( String short_Overview ) {
        this.short_Overview = short_Overview;
    }

    public Long getSeason_number() {
        return season_number;
    }

    public void setSeason_number( Long season_number ) {
        this.season_number = season_number;
    }

    public Season getSeason_id() {
        return season_id;
    }

    public void setSeason_id( Season season_id ) {
        this.season_id = season_id;
    }

}
