package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.account.ChangePassword;
import com.enonic.wem.api.exception.AccountNotFoundException;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static org.junit.Assert.*;

public class ChangePasswordHandlerTest
    extends AbstractCommandHandlerTest
{
    private ChangePasswordHandler handler;

    private AccountDao accountDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        accountDao = Mockito.mock( AccountDao.class );

        this.handler = new ChangePasswordHandler();
        handler.setAccountDao( accountDao );
    }

    @Test
    public void testChangePasswordExistingUser()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = AccountKey.user( "enonic:johndoe" );

        Mockito.when( accountDao.accountExists( this.session, account ) ).thenReturn( true );

        final ChangePassword command = Commands.account().changePassword().key( account ).password( newPassword );
        command.validate();
        this.handler.handle( this.context, command );

        final Boolean passwordChanged = command.getResult();

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
}
