package com.miyava.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import com.google.common.base.Optional;
import com.miyava.common.AfterHandler;
import com.miyava.common.BeforeHandler;
import com.miyava.common.CrudDao;
import com.miyava.user.model.User;
import com.miyava.user.repository.UserRepository;

@RunWith( MockitoJUnitRunner.class )
public class CrudDaoTest {

    @Mock
    private UserRepository repository;

    @Mock
    private BeforeHandler<User> beforeHandler;

    @Mock
    private AfterHandler<User> afterHandler;

    @Mock
    private EntityManager em;

    private boolean validOnSaveOrUpdate;

    private boolean validOnDelete;

    private FoobarDao abstractDao;

    private User user;

    private Errors errors;

    @Before
    public void setup()
        throws Exception {
        abstractDao = new FoobarDao( repository, beforeHandler, afterHandler );
        user = new User();

        when( repository.save( any( User.class ) ) ).thenReturn( new User() );
        Whitebox.setInternalState( abstractDao, "em", em );
    }

    @Test
    public void saveOrUpdate_should_return_null_on_failed_validation()
        throws Exception {
        validOnSaveOrUpdate = false;

        User entity = abstractDao.doSaveOrUpdate( user, errors );

        verify( repository, never() ).save( user );
        assertNull( entity );
    }

    @Test
    public void beforeHandler_should_be_called_before_saveOrUpdate()
        throws Exception {
        abstractDao.doSaveOrUpdate( user, errors );
        verify( beforeHandler ).beforeSaveOrUpdate( any( User.class ), any( Errors.class ) );
    }

    @Test
    public void saveOrUpdate_should_return_newEntity_on_passed_validation()
        throws Exception {
        validOnSaveOrUpdate = true;

        User entity = abstractDao.doSaveOrUpdate( user, errors );

        verify( repository ).save( user );
        assertNotNull( entity );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void afterHandler_should_be_called_after_successful_saveOrUpdate()
        throws Exception {
        validOnSaveOrUpdate = true;
        abstractDao.doSaveOrUpdate( user, errors );
        verify( afterHandler ).afterSaveOrUpdate( any( Optional.class ), any( User.class ), any( Errors.class ) );
    }

    @Test
    public void beforeHandler_should_be_called_before_delete()
        throws Exception {
        abstractDao.doDelete( user, errors );
        verify( beforeHandler ).beforeDelete( any( User.class ), any( Errors.class ) );
    }

    @Test
    public void delete_should_return_false_on_failed_validation()
        throws Exception {
        validOnDelete = false;

        boolean deleted = abstractDao.doDelete( user, errors );

        verify( repository, never() ).delete( user );
        assertFalse( deleted );
    }

    @Test
    public void delete_should_return_true_on_passed_validation()
        throws Exception {
        validOnDelete = true;

        boolean deleted = abstractDao.doDelete( user, errors );

        verify( repository ).delete( user );
        assertTrue( deleted );
    }

    @Test
    public void afterHandler_should_be_called_after_successful_delete()
        throws Exception {
        validOnDelete = true;
        abstractDao.doDelete( user, errors );
        verify( afterHandler ).afterDelete( any( User.class ), any( Errors.class ) );
    }

    class FoobarDao
        extends CrudDao<User, String, UserRepository> {

        public FoobarDao( UserRepository repository, BeforeHandler<User> beforeHandler,
                          AfterHandler<User> afterHandler ) {
            super( repository, beforeHandler, afterHandler );
        }

        @Override
        public boolean validateOnSaveOrUpdate( Optional<User> oldEntity, User entity, Errors errors ) {
            return validOnSaveOrUpdate;
        }

        @Override
        public boolean validateOnDelete( User entity, Errors errors ) {
            return validOnDelete;
        }
    }
}
