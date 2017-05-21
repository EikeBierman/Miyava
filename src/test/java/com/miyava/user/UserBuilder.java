package com.miyava.user;

import java.util.Set;

import com.miyava.user.model.User;
import com.miyava.user.model.Role;

public class UserBuilder {
    private User user;

    public UserBuilder( User user ) {
        this.user = user;
    }

    public static UserBuilder newUser( String id ) {
        User user = new User();
        user.setId( id );
        return new UserBuilder( user );
    }

    public UserBuilder withUsername( String username ) {
        user.setUsername( username );

        return this;
    }

    public UserBuilder withPassword( String password ) {
        user.setPassword( password );
        return this;
    }

    public UserBuilder withPasswordConfirm( String passwordConfirm ) {
        user.setPasswordConfirm( passwordConfirm );
        return this;
    }

    public UserBuilder withRolle( Set<Role> userRoles ) {
        user.setUserRoles( userRoles );

        return this;
    }

    public User build() {
        return user;
    }

}