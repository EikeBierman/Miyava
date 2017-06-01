package com.miyava.movie.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.miyava.auditing.AuditedEntity;
import com.miyava.common.NotEmpty;
import com.miyava.genres.model.Genres;

@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
@Entity
public class Movie
    extends AuditedEntity
    implements com.miyava.common.Entity<Long> {

    @Column( name = "movie_id" )
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @JsonView( DataTablesOutput.View.class )
    private Long id;

    @NotEmpty( message = "movie.messages.title_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String title;

    @NotEmpty( message = "movie.messages.description_empty" )
    @Column( nullable = false, unique = false )
    @Lob
    @JsonView( DataTablesOutput.View.class )
    private String overview;

    @Column( nullable = true, unique = false )
    @JsonView( DataTablesOutput.View.class )
    private String short_Overview;

    @NotEmpty( message = "movie.messages.poster_Path_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String poster_path;

    @NotEmpty( message = "movie.messages.runtime_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String runtime;

    @NotEmpty( message = "movie.messages.status_empty" )
    @Column( nullable = false, unique = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String status;

    @Column( unique = false, columnDefinition = "DATETIME", name = "release_date" )
    @JsonView( DataTablesOutput.View.class )
    private Date release_date;

    @ManyToMany( cascade = CascadeType.ALL, targetEntity = Genres.class )
    @JoinTable( name = "movie_genres", joinColumns = @JoinColumn( name = "movie_id", referencedColumnName = "movie_id" ), inverseJoinColumns = @JoinColumn( name = "genres_id", referencedColumnName = "genres_id" ) )
    @JsonView( DataTablesOutput.View.class )
    private List<Genres> genres;

    @OneToMany( mappedBy = "movie" )
    private List<UserMovie> userMovie = new ArrayList<UserMovie>();

    public Movie() {}

    public Movie( String title, String overview, String short_Overview, String status, Date release_date, List<Genres> genres ) {
        super();
        this.title = title;
        this.overview = overview;
        this.short_Overview = short_Overview;
        this.status = status;
        this.release_date = release_date;
        this.genres = genres;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getShortOverview() {
        return short_Overview;
    }

    public void setShortOverview( String short_Overview ) {
        this.short_Overview = short_Overview;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview( String overview ) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path( String poster_path ) {
        this.poster_path = poster_path;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime( String runtime ) {
        this.runtime = runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public void setRelease_date( Date release_date ) {
        this.release_date = release_date;
    }

    public List<Genres> getGenres() {
        return genres;
    }

    public void setGenres( List<Genres> genres ) {
        this.genres = genres;
    }

    public List<UserMovie> getUserMovie() {
        return userMovie;
    }

    public void setUserMovie( List<UserMovie> userMovie ) {
        this.userMovie = userMovie;
    }
}
