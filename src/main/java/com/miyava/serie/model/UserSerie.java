package com.miyava.serie.model;

import com.miyava.user.model.User;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table( name = " User_Serie" )
public class UserSerie {
    @Id
    @GeneratedValue
    @Column( name = "user_Episode_id" )
    private Long id;

    @ManyToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "serie_id" )
    private Serie serie;

    @OneToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "episode_id" )
    private Episode episode;

    @ManyToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "user_id" )
    private User user;

    @Column( name = "watched_date" )
    @Temporal( TemporalType.DATE )
    private Date watchedDate;

    public UserSerie() {}

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie( Serie serie ) {
        this.serie = serie;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode( Episode episode ) {
        this.episode = episode;
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
