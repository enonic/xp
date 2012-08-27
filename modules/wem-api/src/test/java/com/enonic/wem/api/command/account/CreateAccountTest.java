package com.enonic.wem.api.command.account;

import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

public class CreateAccountTest
{
    @Test
    public void testValid()
    {
        final UserAccount account = UserAccount.create( "enonic:user1" );
        final CreateAccount command = new CreateAccount();

        command.account( account );
        assertSame( account, command.getAccount() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullAccount()
    {
        final CreateAccount command = new CreateAccount();
        command.account( null );

        command.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testNotValid_roleAccount()
    {
        final RoleAccount account = RoleAccount.create( "enonic:myrole" );
        final CreateAccount command = new CreateAccount();
        command.account( account );

        command.validate();
    }
}
