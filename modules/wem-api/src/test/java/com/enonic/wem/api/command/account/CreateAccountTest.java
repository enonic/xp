package com.enonic.wem.api.command.account;

import org.junit.Test;

import static org.junit.Assert.*;

import org.mockito.Mockito;

import com.enonic.wem.api.account.UserAccount;

public class CreateAccountTest
{
    @Test
    public void testValid()
    {
        final UserAccount account = Mockito.mock( UserAccount.class );
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
}
