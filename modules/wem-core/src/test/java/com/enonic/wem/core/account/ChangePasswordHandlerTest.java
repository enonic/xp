package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class ChangePasswordHandlerTest
{
    private Client client;

    private UserDao userDao;

    private SecurityService securityService;

    @Before
    public void setUp()
        throws Exception
    {
        userDao = Mockito.mock( UserDao.class );
        securityService = Mockito.mock( SecurityService.class );

        final ChangePasswordHandler changePasswordHandler = new ChangePasswordHandler();
        changePasswordHandler.setUserDao( userDao );
        changePasswordHandler.setSecurityService( securityService );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( changePasswordHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }


    @Test
    public void testChangePasswordExistingUser()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = AccountKey.from( "user:enonic:johndoe" );

        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        mockAddUserToDaoByQualifiedName( user );

        final Boolean passwordChanged = client.execute( Commands.account().changePassword().key( account ).password( newPassword ) );

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
        final AccountKey account = AccountKey.from( "user:enonic:johndoe" );

        client.execute( Commands.account().changePassword().key( account ).password( newPassword ) );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testChangePasswordNonUserAccount()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = AccountKey.from( "group:enonic:devs" );

        client.execute( Commands.account().changePassword().key( account ).password( newPassword ) );
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

    private class IsQualifiedUsername
        extends ArgumentMatcher<QualifiedUsername>
    {
        private final QualifiedUsername qualifiedName;

        public IsQualifiedUsername( final QualifiedUsername qualifiedName )
        {
            this.qualifiedName = qualifiedName;
        }

        public boolean matches( Object other )
        {
            final QualifiedUsername otherQualifiedName = (QualifiedUsername) other;
            return this.qualifiedName.getUsername().equals( otherQualifiedName.getUsername() ) &&
                this.qualifiedName.getUserStoreName().equals( otherQualifiedName.getUserStoreName() );
        }
    }
}
