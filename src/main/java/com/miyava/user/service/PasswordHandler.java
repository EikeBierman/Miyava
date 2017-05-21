package com.miyava.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.google.common.base.Strings;
import com.miyava.common.BeforeHandler;
import com.miyava.user.model.User;

@Component
public class PasswordHandler
    implements BeforeHandler<User> {

    private PasswordEncoder passwordEncoder;

    @Autowired
    PasswordHandler( PasswordEncoder passwordEncoder ) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void beforeSave( User entity, Errors errors ) {
        String hashedPassword = passwordEncoder.encode( entity.getPassword() );
        entity.setPassword( hashedPassword );
    }

    @Override
    public void beforeUpdate( User entity, Errors errors ) {
        if ( !Strings.isNullOrEmpty( entity.getPassword() ) ) {
            String hashedPassword = passwordEncoder.encode( entity.getPassword() );
            entity.setPassword( hashedPassword );
        }
    }

    @Override
    public void beforeSaveOrUpdate( User entity, Errors errors ) {
        // do nothing
    }

    @Override
    public void beforeDelete( User entity, Errors errors ) {
        // do nothing
    }

}
