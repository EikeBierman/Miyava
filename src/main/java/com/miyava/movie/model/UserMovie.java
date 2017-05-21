package com.miyava.movie.model;

import com.miyava.user.model.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table( name = " user_movie" )
public class UserMovie {
    @Id
    @GeneratedValue
    @Column( name = "user_movie_id" )
    private Long id;

    @ManyToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "movie_id" )
    private Movie movie;

    @ManyToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "user_id" )
    private User user;

    @Column( name = "watched_date" )
    @Temporal( TemporalType.DATE )
    private Date watchedDate;

    public UserMovie() {}

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie( Movie movie ) {
        this.movie = movie;
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    public Date getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate( Date watchedDate ) {
        this.watchedDate = watchedDate;
    }
}
