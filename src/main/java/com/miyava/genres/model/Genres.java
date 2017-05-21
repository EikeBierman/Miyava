package com.miyava.genres.model;

import java.util.List;
import javax.persistence.*;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.miyava.auditing.AuditedEntity;
import com.miyava.common.NotEmpty;
import com.miyava.movie.model.Movie;

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class, 
    property = "id")
@Entity
public class Genres
    extends AuditedEntity
    implements com.miyava.common.Entity<Long> {

    @Column( name = "genres_id" )
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @JsonView( DataTablesOutput.View.class )
    private Long id;

    @NotEmpty( message = "Das Feld darf nicht Leer sein!" )
    @Column( nullable = false, unique = true, length = 100 )
    @Length( max = 100, message = "Name ist zu lang" )
    @JsonView( DataTablesOutput.View.class )
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, targetEntity = Movie.class)
    @JoinTable(name = "movie_genres",
    inverseJoinColumns = @JoinColumn(name = "movie_id",
            nullable = false,
            updatable = false),
    joinColumns = @JoinColumn(name = "genres_id",
            nullable = false,
            updatable = false))
    private List<Movie> movies;
    
    public Genres() {}

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

    public List<Movie> getMovie() {
        return movies;
    }

    public void setMovie(List<Movie> movies) {
        this.movies = movies;
    }
    
}
