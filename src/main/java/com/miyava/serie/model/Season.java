package com.miyava.serie.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.auditing.AuditedEntity;

@Entity
@Table( name = "season" )
public class Season
    extends AuditedEntity
    implements com.miyava.common.Entity<Long> {

    @Id
    @GeneratedValue
    @Column( name = "season_serie_id" )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "serie_id" )
    private Serie serie_id;

    @OneToMany( mappedBy = "season_id", cascade = CascadeType.ALL )
    private List<Episode> episodes;

    @Column( nullable = true, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String name;

    @Column( nullable = true, unique = false )
    @Lob
    @JsonView( DataTablesOutput.View.class )
    private String overview;

    @Column( nullable = true, unique = false )
    @JsonView( DataTablesOutput.View.class )
    private String short_Overview;

    @Column( unique = false, columnDefinition = "DATETIME", name = "air_date" )
    @JsonView( DataTablesOutput.View.class )
    private Date air_date;

    @Column( nullable = true, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String poster_path;

    @Column( name = "seasonNumber" )
    private Long season_number;

    public Season() {

    }

    public Season( String name ) {
        this.name = name;
    }

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

    public Date getAir_date() {
        return air_date;
    }

    public void setAir_date( Date air_date ) {
        this.air_date = air_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path( String poster_path ) {
        this.poster_path = poster_path;
    }

    public Long getSeason_number() {
        return season_number;
    }

    public void setSeason_number( Long season_number ) {
        this.season_number = season_number;
    }

    public Serie getSerie_id() {
        return serie_id;
    }

    public void setSerie_id( Serie serie_id ) {
        this.serie_id = serie_id;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes( List<Episode> episodes ) {
        this.episodes = episodes;
    }
}
