package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.ValidatePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class ValidatePasswordHandlerTest
    extends AbstractCommandHandlerTest
{
    private AccountDao accountDao;

    private ValidatePasswordHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        this.handler = new ValidatePasswordHandler();
        this.handler.setAccountDao( accountDao );
    }

    @Test
    public void testValidatePasswordExistingUserCorrectPassword()
        throws Exception
    {
        // setup
        final String userPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );

        // exercise
        final ValidatePassword command = Commands.account().validatePassword().key( account ).password( userPassword );
        command.validate();
        this.handler.handle( this.context, command );
        final Boolean validPassword = command.getResult();

        // verify
        assertNotNull( validPassword );
        assertTrue( validPassword );
    }

    @Test
    @Ignore // TODO enable test when password validation is implemented in ValidatePasswordHandler
    public void testValidatePasswordExistingUserWrongPassword()
        throws Exception
    {
        // setup
        final String userPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );
        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );

        // exercise
        final ValidatePassword command = Commands.account().validatePassword().key( account ).password( "forgotPassword" );
        command.validate();
        this.handler.handle( this.context, command );
        final Boolean validPassword = command.getResult();

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

        final ValidatePassword command = Commands.account().validatePassword().key( account ).password( userPassword );
        command.validate();
        this.handler.handle( this.context, command );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatePasswordNonUserAccount()
        throws Exception
    {
        final String userPassword = "passw0rd";
        final AccountKey account = AccountKey.group( "enonic:devs" );

        // validation fails before attempting to execute command (cannot validate password of a group)
        final ValidatePassword command = Commands.account().validatePassword().key( account ).password( userPassword );
        command.validate();
    }

}
