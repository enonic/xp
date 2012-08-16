package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.client.StandardClient;
import com.enonic.wem.core.command.CommandInvokerImpl;

import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;

public class ValidatePasswordHandlerTest
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

        final ValidatePasswordHandler validatePasswordHandler = new ValidatePasswordHandler();
        validatePasswordHandler.setUserDao( userDao );
        validatePasswordHandler.setSecurityService( securityService );

        final StandardClient standardClient = new StandardClient();
        final CommandInvokerImpl commandInvoker = new CommandInvokerImpl();
        commandInvoker.setHandlers( validatePasswordHandler );
        standardClient.setInvoker( commandInvoker );
        client = standardClient;
    }


    @Test
    public void testValidatePasswordExistingUserCorrectPassword()
        throws Exception
    {
        final String userPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        mockAddUserToDaoByQualifiedName( user );

        final InvalidCredentialsException ex = new InvalidCredentialsException( user.getQualifiedName() );
        final QualifiedUsername qualifiedName = user.getQualifiedName();
        doThrow( ex ).doNothing().when( securityService ).loginPortalUser( Mockito.argThat( new IsQualifiedUsername( qualifiedName ) ),
                                                                           not( eq( userPassword ) ) );

        // exercise
        final Boolean validPassword = client.execute( Commands.account().validatePassword().key( account ).password( userPassword ) );

        // verify
        assertNotNull( validPassword );
        assertTrue( validPassword );
    }

    @Test
    public void testValidatePasswordExistingUserWrongPassword()
        throws Exception
    {
        final String userPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        // setup
        final UserEntity user = createUser( "ASDD8F", account.getUserStore(), account.getLocalName() );
        mockAddUserToDaoByQualifiedName( user );

        final InvalidCredentialsException ex = new InvalidCredentialsException( user.getQualifiedName() );
        final QualifiedUsername qualifiedName = user.getQualifiedName();
        doThrow( ex ).doNothing().when( securityService ).loginPortalUser( Mockito.argThat( new IsQualifiedUsername( qualifiedName ) ),
                                                                           not( eq( userPassword ) ) );

        // exercise
        final Boolean validPassword = client.execute( Commands.account().validatePassword().key( account ).password( "forgotPassword" ) );

        // verify
        assertNotNull( validPassword );
        assertFalse( validPassword );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testValidatePasswordNonExistingUser()
        throws Exception
    {
        final String userPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        client.execute( Commands.account().validatePassword().key( account ).password( userPassword ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatePasswordNonUserAccount()
        throws Exception
    {
        final String userPassword = "passw0rd";
        final AccountKey account = AccountKey.group( "enonic:devs" );

        // validation fails before attempting to execute command (cannot validate password of a group)
        client.execute( Commands.account().validatePassword().key( account ).password( userPassword ) );
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
