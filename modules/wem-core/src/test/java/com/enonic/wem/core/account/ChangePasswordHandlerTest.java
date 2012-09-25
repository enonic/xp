package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.ChangePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class ChangePasswordHandlerTest
    extends AbstractCommandHandlerTest
{
    private ChangePasswordHandler handler;

    private UserDao userDao;

    private SecurityService securityService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setup();

        userDao = Mockito.mock( UserDao.class );
        securityService = Mockito.mock( SecurityService.class );

        this.handler = new ChangePasswordHandler();
        handler.setUserDao( userDao );
        handler.setSecurityService( securityService );
    }

    @Test
    public void testChangePasswordExistingUser()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        mockAddUserToDaoByQualifiedName( user );

        final ChangePassword command = Commands.account().changePassword().key( account ).password( newPassword );
        command.validate();
        this.handler.handle( this.context, command );

        final Boolean passwordChanged = command.getResult();

        verify( securityService, atLeastOnce() ).changePassword( Mockito.argThat( new IsQualifiedUsername( user.getQualifiedName() ) ),
                                                                 eq( newPassword ) );
        assertNotNull( passwordChanged );
        assertTrue( passwordChanged );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testChangePasswordNonExistingUser()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        final ChangePassword command = Commands.account().changePassword().key( account ).password( newPassword );
        command.validate();
        this.handler.handle( this.context, command );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordNonUserAccount()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = AccountKey.group( "enonic:devs" );

        // validation fails before attempting to execute command (cannot change password of a group)
        final ChangePassword command = Commands.account().changePassword().key( account ).password( newPassword );
        command.validate();
    }

    private void mockAddUserToDaoByQualifiedName( final UserEntity user )
    {
        Mockito.when( userDao.findByQualifiedUsername( Mockito.argThat( new IsQualifiedUsername( user.getQualifiedName() ) ) ) ).thenReturn(
            user );
    }

    private UserEntity createUser( final String key, final String userStore, final String name )
        throws Exception
    {
        final UserEntity user = Mockito.mock( UserEntity.class, Mockito.CALLS_REAL_METHODS );
        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserStore( userStore ) );
        user.setName( name );
        user.setDisplayName( name + " User" );
        return user;
    }

    private UserStoreEntity createUserStore( final String name )
    {
        final UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( name );
        return userStore;
    }
}
