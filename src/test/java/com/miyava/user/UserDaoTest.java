package com.miyava.user;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;

import com.google.common.collect.Sets;
import com.miyava.user.model.User;
import com.miyava.user.repository.UserRepository;
import com.miyava.user.service.PasswordHandler;
import com.miyava.user.service.UserDao;
import com.miyava.user.model.Role;

@RunWith( MockitoJUnitRunner.class )
public class UserDaoTest {

    private final Integer passwordMinlength = 6;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordHandler passwordHandler;

    @Mock
    private Errors errors;

    private UserDao userDao;

    private BCryptPasswordEncoder passwordEncoder;

    @Before
    public void setup()
        throws Exception {
        passwordEncoder = new BCryptPasswordEncoder();
        userDao = new UserDao( userRepo, passwordHandler, passwordEncoder, passwordMinlength );
    }

    @Test
    public void validate_on_save_or_update_should_always_reject()
        throws Exception {
        boolean valid = userDao.validateOnSaveOrUpdate( null, UserBuilder.newUser( "test" ).build(), errors );
        assertFalse( valid );
    }

    @Test
    public void validate_on_delete_with_empty_value_should_reject()
        throws Exception {
        boolean valid = userDao.validateOnDelete( null, errors );
        assertFalse( valid );
    }

    @Test
    public void validate_on_delete_with_non_empty_value_should_accept()
        throws Exception {
        boolean valid = userDao.validateOnDelete( UserBuilder.newUser( "test" ).build(), errors );
        assertTrue( valid );
    }

    @Test
    public void validate_on_save_with_empty_value_should_reject()
        throws Exception {
        boolean valid = userDao.validateOnSave( null, errors );
        assertFalse( valid );
    }

    @Test
    public void save_should_reject_empty_password()
        throws Exception {
        boolean valid = userDao.validateOnSave( UserBuilder.newUser( "test" ).build(), errors );
        assertFalse( valid );
        verify( errors ).rejectValue( "password", "user.messages.password_empty" );
    }

    @Test
    public void save_should_reject_different_passwords()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "foobar" ) )
            .withPasswordConfirm( "barfoo" ).build();

        boolean valid = userDao.validateOnSave( user, errors );

        assertFalse( valid );
        verify( errors ).rejectValue( "passwordConfirm", "user.messages.passwords_dont_match" );
    }

    @Test
    public void save_should_reject_password_min_length()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "foo" ) )
            .withPasswordConfirm( "foo" ).build();

        boolean valid = userDao.validateOnSave( user, errors );

        assertFalse( valid );
        verify( errors ).rejectValue( "password", "user.messages.password_minlength", new Object[] { passwordMinlength.toString() }, null );
    }

    @Test
    public void save_should_accept_matching_passwords()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) )
            .withPasswordConfirm( "qwertz" ).withRolle( Sets.newHashSet( Role.ROLE_ADMIN ) ).build();

        boolean valid = userDao.validateOnSave( user, errors );

        assertTrue( valid );
    }

    @Test
    public void save_should_reject_without_roles()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) )
            .withPasswordConfirm( "qwertz" ).withRolle( null ).build();

        boolean valid = userDao.validateOnSave( user, errors );

        assertFalse( valid );
        verify( errors ).rejectValue( "userRoles", "user.messages.role_empty" );
    }

    @Test
    public void create_user_should_reject_with_existing_user()
        throws Exception {
        when( userDao.findOneByUsername( anyString() ) ).thenReturn( UserBuilder.newUser( "dummy" ).build() );

        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) )
            .withPasswordConfirm( "qwertz" ).withRolle( Sets.newHashSet( Role.ROLE_ADMIN ) ).build();

        boolean valid = userDao.validateOnSave( user, errors );
        assertFalse( valid );
        verify( errors ).rejectValue( "username", "user.messages.id_already_exists", new Object[] { user.getUsername() },
            null );
    }

    @Test
    public void validate_on_update_with_empty_value_should_reject()
        throws Exception {
        boolean valid = userDao.validateOnUpdate( null, null, errors );
        assertFalse( valid );
    }

    @Test
    public void update_should_reject_without_roles()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) )
            .withPasswordConfirm( "qwertz" ).withRolle( null ).build();

        boolean valid = userDao.validateOnUpdate( user, user, errors );

        assertFalse( valid );
        verify( errors ).rejectValue( "userRoles", "user.messages.role_empty" );
    }

    @Test
    public void update_user_should_reject_with_existing_user()
        throws Exception {
        when( userDao.findOneByUsername( anyString() ) ).thenReturn( UserBuilder.newUser( "dummy" ).build() );

        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) )
            .withPasswordConfirm( "qwertz" ).withRolle( Sets.newHashSet( Role.ROLE_ADMIN ) ).build();

        boolean valid = userDao.validateOnSave( user, errors );
        assertFalse( valid );
        verify( errors ).rejectValue( "username", "user.messages.id_already_exists", new Object[] { user.getUsername() },
            null );
    }

    @Test
    public void update_should_accept_empty_password_and_set_existing()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withRolle( Sets.newHashSet( Role.ROLE_ADMIN ) ).build();

        User userWithPassword = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) ).build();

        boolean valid = userDao.validateOnUpdate( userWithPassword, user, errors );

        assertTrue( valid );
        assertThat( user.getPassword(), equalTo( userWithPassword.getPassword() ) );
    }

    @Test
    public void update_should_reject_different_passwords()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) )
            .withPasswordConfirm( "trewqz" ).withRolle( Sets.newHashSet( Role.ROLE_ADMIN ) ).build();

        User userWithPassword = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) ).build();

        boolean valid = userDao.validateOnUpdate( userWithPassword, user, errors );

        assertFalse( valid );
        verify( errors ).rejectValue( "passwordConfirm", "user.messages.passwords_dont_match" );
    }

    @Test
    public void update_should_accept_matching_passwords()
        throws Exception {
        User user = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) )
            .withPasswordConfirm( "qwertz" ).withRolle( Sets.newHashSet( Role.ROLE_ADMIN ) ).build();

        User userWithPassword = UserBuilder.newUser( "test" ).withPassword( passwordEncoder.encode( "qwertz" ) ).build();
        boolean valid = userDao.validateOnUpdate( userWithPassword, user, errors );

        assertTrue( valid );
    }

}
