package com.miyava.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.miyava.common.CrudDao;
import com.miyava.user.dto.UserDTO;
import com.miyava.user.dto.UserDTOMapper;
import com.miyava.user.model.User;
import com.miyava.user.repository.UserRepository;

@Service
public class UserDao
    extends CrudDao<User, String, UserRepository> {

    private final PasswordEncoder passwordEncoder;

    private final Integer passwordMinlength;

    @Autowired
    public UserDao( UserRepository repository,
                    PasswordHandler passwordHandler,
                    PasswordEncoder passwordEncoder,
                    @Value( "${user.password_minlength}" ) Integer passwordMinLength ) {
        super( repository, passwordHandler );
        this.passwordEncoder = passwordEncoder;
        this.passwordMinlength = Preconditions.checkNotNull( passwordMinLength );
    }

    /**
     * Does nothing and returns false.
     */
    @Override
    public boolean validateOnSaveOrUpdate( Optional<User> oldEntity, User entity, Errors errors ) {
        return false;
    }

    @Override
    public boolean validateOnDelete( User entity, Errors errors ) {
        if ( entity != null ) {
            return true;
        }
        return false;
    }

    @Override
    public boolean validateOnSave( User entity, Errors errors ) {
        if ( entity == null ) {
            return false;
        }

        if ( !validatePassword( entity, errors ) ) {
            return false;
        }

        // Rollen
        if ( entity.getUserRoles() == null || entity.getUserRoles().isEmpty() ) {
            errors.rejectValue( "userRoles", "user.messages.role_empty" );
            return false;
        }

        // Username bereits vergeben?
        if ( findOneByUsername( entity.getUsername() ) != null ) {
            errors.rejectValue( "username", "user.messages.id_already_exists", new Object[] { entity.getUsername() }, null );
            return false;
        }

        return true;
    }

    private boolean validatePassword( User entity, Errors errors ) {
        // validate password manually, since it can be empty/null on updates
        if ( Strings.isNullOrEmpty( entity.getPassword() ) ) {
            errors.rejectValue( "password", "user.messages.password_empty" );
            return false;
        }

        if ( !passwordEncoder.matches( entity.getPasswordConfirm(), entity.getPassword() ) ) {
            errors.rejectValue( "passwordConfirm", "user.messages.passwords_dont_match" );
            return false;
        }

        // Passwort Länge
        if ( entity.getPasswordConfirm().length() < passwordMinlength ) {
            errors.rejectValue( "password", "user.messages.password_minlength", new Object[] { passwordMinlength.toString() }, null );
            return false;
        }

        return true;
    }

    @Override
    public boolean validateOnUpdate( User oldEntity, User entity, Errors errors ) {
        if ( entity == null ) {
            return false;
        }

        // Rollen
        if ( entity.getUserRoles() == null || entity.getUserRoles().isEmpty() ) {
            errors.rejectValue( "userRoles", "user.messages.role_empty" );
            return false;
        }

        // Username bereits vergeben?
        User tmpUser = findOneByUsername( entity.getUsername() );
        if ( tmpUser != null && !tmpUser.getId().equals( entity.getId() ) ) {
            errors.rejectValue( "username", "user.messages.id_already_exists", new Object[] { entity.getUsername() }, null );
            return false;
        }

        if ( Strings.isNullOrEmpty( entity.getPassword() ) && Strings.isNullOrEmpty( entity.getPasswordConfirm() ) ) {
            entity.setPassword( oldEntity.getPassword() );
            return true;
        }

        if ( entity.getPassword() != null && entity.getPasswordConfirm() != null ) {
            if ( !validatePassword( entity, errors ) ) {
                return false;
            }
        }

        return true;
    }

    public User findOneByUsername( String username ) {
        return repository.findOneByUsername( username );
    }

    public List<UserDTO> transformFiles( List<User> users ) {
        return Lists.newArrayList( Iterables.transform( users, new UserDTOMapper() ) );
    }
}
