package com.miyava.user.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.auditing.AuditedEntityWithUUID;
import com.miyava.common.NotEmpty;
import com.miyava.movie.model.UserMovie;
import com.miyava.serie.model.UserSerie;

@Entity
public class User
    extends AuditedEntityWithUUID {

    @NotEmpty( message = "user.messages.username_empty" )
    @Email( message = "user.messages.username_wrong_format" )
    @Column( nullable = false, unique = true, length = 100 )
    @Length( max = 100, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String email;

    @NotEmpty( message = "user.messages.username_empty" )
    @Column( nullable = false, unique = true, length = 100 )
    @Length( max = 100, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String username;

    @Column( nullable = false, length = 255 )
    @Length( max = 255, message = "common.message.data_to_long" )
    private String password;

    @Transient
    private String passwordOld;

    @Transient
    private String passwordConfirm;

    @JsonView( DataTablesOutput.View.class )
    private boolean enabled;

    @NotEmpty( message = "user.messages.firstname_empty" )
    @Column( length = 100 )
    @Length( max = 100, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String firstname;

    @NotEmpty( message = "user.messages.lastname_empty" )
    @Column( length = 100 )
    @Length( max = 100, message = "common.message.data_to_long" )
    @JsonView( DataTablesOutput.View.class )
    private String lastname;

    // @NotEmpty( message = "user.messages.birthday" )
    // private Date birthday;

    @ElementCollection( fetch = FetchType.EAGER )
    @JoinTable( name = "user_sites", joinColumns = @JoinColumn( name = "id" ) )
    @Column( nullable = false )
    @Enumerated( EnumType.STRING )
    @JsonView( DataTablesOutput.View.class )
    private Set<Site> userSite;

    @ElementCollection( fetch = FetchType.EAGER )
    @JoinTable( name = "user_roles", joinColumns = @JoinColumn( name = "id" ) )
    @Column( nullable = false )
    @Enumerated( EnumType.STRING )
    @JsonView( DataTablesOutput.View.class )
    private Set<Role> userRoles;

    @OneToMany( mappedBy = "user" )
    @JsonView( DataTablesOutput.View.class )
    private List<UserMovie> userMovies = new ArrayList<UserMovie>();

    @OneToMany( mappedBy = "user" )
    @JsonView( DataTablesOutput.View.class )
    private List<UserSerie> userSeries = new ArrayList<UserSerie>();

    public User() {
        // needed for serialization
    }

    public User( String id ) {
        super( id );
    }

    public User( String email, String username, String password, String passwordOld, String passwordConfirm, boolean enabled,
                 String firstname, String lastname, Set<Site> userSite, Set<Role> userRoles ) {
        super();
        this.email = email;
        this.username = username;
        this.password = password;
        this.passwordOld = passwordOld;
        this.passwordConfirm = passwordConfirm;
        this.enabled = enabled;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userSite = userSite;
        this.userRoles = userRoles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname( String firstname ) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname( String lastname ) {
        this.lastname = lastname;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm( String passwordConfirm ) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<Site> getUserSite() {
        if ( userSite == null ) {
            userSite = new HashSet<>();
        }
        return userSite;
    }

    public void setUserSite( Set<Site> userSite ) {
        this.userSite = userSite;
    }

    public Set<Role> getUserRoles() {
        if ( userRoles == null ) {
            userRoles = new HashSet<>();
        }
        return userRoles;
    }

    public void setUserRoles( Set<Role> userRoles ) {
        this.userRoles = userRoles;
    }

    public String getPasswordOld() {
        return passwordOld;
    }

    public void setPasswordOld( String passwordOld ) {
        this.passwordOld = passwordOld;
    }

    public List<UserMovie> getUserMovies() {
        return userMovies;
    }

    public void setUserMovies( List<UserMovie> userMovies ) {
        this.userMovies = userMovies;
    }

    public List<UserSerie> getUserSeries() {
        return userSeries;
    }

    public void setUserSeries( List<UserSerie> userSeries ) {
        this.userSeries = userSeries;
    }

}
