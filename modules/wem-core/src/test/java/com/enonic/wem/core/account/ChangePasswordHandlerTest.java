package com.enonic.wem.core.account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.GroupKey;
import com.enonic.wem.api.account.UserKey;
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
        this.handler.setContext( this.context );
        this.handler.setAccountDao( accountDao );
    }

    @Test
    public void testChangePasswordExistingUser()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = UserKey.from( "enonic:johndoe" );

        Mockito.when( accountDao.accountExists( account, this.session ) ).thenReturn( true );

        final ChangePassword command = Commands.account().changePassword().key( account ).password( newPassword );
        command.validate();
        this.handler.handle( command );

        final Boolean passwordChanged = command.getResult();

        assertNotNull( passwordChanged );
        assertTrue( passwordChanged );
    }

    @Test(expected = AccountNotFoundException.class)
    public void testChangePasswordNonExistingUser()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = UserKey.from( "enonic:johndoe" );

        final ChangePassword command = Commands.account().changePassword().key( account ).password( newPassword );
        command.validate();
        this.handler.handle( command );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordNonUserAccount()
        throws Exception
    {
        final String newPassword = "passw0rd";
        final AccountKey account = GroupKey.from( "enonic:devs" );

        // validation fails before attempting to execute command (cannot change password of a group)
        final ChangePassword command = Commands.account().changePassword().key( account ).password( newPassword );
        command.validate();
    }
}
