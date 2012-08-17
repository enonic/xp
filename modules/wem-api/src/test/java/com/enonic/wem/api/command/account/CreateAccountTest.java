package com.enonic.wem.api.command.account;

import org.junit.Test;

import static org.junit.Assert.*;

import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserAccount;

public class CreateAccountTest
{
    @Test
    public void testValid()
    {
        final UserAccount account = Mockito.mock( UserAccount.class );
        final AccountKey accountKey = AccountKey.user( "enonic:user1" );
        Mockito.when( account.getKey() ).thenReturn( accountKey );
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
        final UserAccount account = Mockito.mock( UserAccount.class );
        final AccountKey accountKey = AccountKey.role( "enonic:myrole" );
        Mockito.when( account.getKey() ).thenReturn( accountKey );
        final CreateAccount command = new CreateAccount();
        command.account( account );

        command.validate();
    }
}
