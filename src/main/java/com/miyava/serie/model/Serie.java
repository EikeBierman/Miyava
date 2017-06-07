package com.miyava.serie.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.auditing.AuditedEntity;
import com.miyava.common.NotEmpty;
import com.miyava.genres.model.Genres;

@Entity
@Table( name = "serie" )
public class Serie
    extends AuditedEntity
    implements com.miyava.common.Entity<Long> {

    @Column( name = "serie_id" )
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @JsonView( DataTablesOutput.View.class )
    private Long id;

    @NotEmpty( message = "serie.messages.name_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String name;

    @OneToMany( mappedBy = "serie_id", cascade = CascadeType.ALL )
    private List<Season> seasons;

    @Column( name = "theSerieDbId" )
    private Long theSerieDbId;

    @Column( name = "numberOfSeasons" )
    private Long number_of_seasons;

    @Column( name = "numberOfEpisodes" )
    private Long number_of_episodes;

    @NotEmpty( message = "serie.messages.description_empty" )
    @Column( nullable = false, unique = false )
    @Lob
    @JsonView( DataTablesOutput.View.class )
    private String overview;

    @Column( nullable = true, unique = false )
    @JsonView( DataTablesOutput.View.class )
    private String short_Overview;

    @NotEmpty( message = "serie.messages.poster_Path_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String poster_path;

    @NotEmpty( message = "serie.messages.status_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String status;

    @Column( unique = false, columnDefinition = "DATETIME", name = "first_air_date" )
    @JsonView( DataTablesOutput.View.class )
    private Date first_air_date;

    @Column( unique = false, columnDefinition = "DATETIME", name = "last_air_date" )
    @JsonView( DataTablesOutput.View.class )
    private Date last_air_date;

    @ManyToMany( cascade = CascadeType.ALL, targetEntity = Genres.class )
    @JoinTable( name = "serie_genres", joinColumns = @JoinColumn( name = "serie_id", referencedColumnName = "serie_id" ), inverseJoinColumns = @JoinColumn( name = "genres_id", referencedColumnName = "genres_id" ) )
    @JsonView( DataTablesOutput.View.class )
    private List<Genres> genres;

    public Serie() {}

    public Serie( String name, Season season ) {}

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

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons( List<Season> seasons ) {
        this.seasons = seasons;
    }

    public Long getTheSerieDbId() {
        return theSerieDbId;
    }

    public void setTheSerieDbId( Long theSerieDbId ) {
        this.theSerieDbId = theSerieDbId;
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

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path( String poster_path ) {
        this.poster_path = poster_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public Date getFirst_air_date() {
        return first_air_date;
    }

    public void setFirst_air_date( Date first_air_date ) {
        this.first_air_date = first_air_date;
    }

    public Date getLast_air_date() {
        return last_air_date;
    }

    public void setLast_air_date( Date last_air_date ) {
        this.last_air_date = last_air_date;
    }

    public List<Genres> getGenres() {
        return genres;
    }

    public void setGenres( List<Genres> genres ) {
        this.genres = genres;
    }

    public Long getNumber_of_seasons() {
        return number_of_seasons;
    }

    public void setNumber_of_seasons( Long number_of_seasons ) {
        this.number_of_seasons = number_of_seasons;
    }

    public Long getNumber_of_episodes() {
        return number_of_episodes;
    }

    public void setNumber_of_episodes( Long number_of_episodes ) {
        this.number_of_episodes = number_of_episodes;
    }
}
