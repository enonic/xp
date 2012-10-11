package com.enonic.wem.api.command.account;

import org.junit.Test;

import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

import static org.junit.Assert.*;

public class CreateAccountTest
{
    @Test
    public void testValidUser()
    {
        final UserAccount account = UserAccount.create( "enonic:user1" );
        final CreateAccount command = new CreateAccount();

        command.account( account );
        assertSame( account, command.getAccount() );

        command.validate();
    }

    @Test
    public void testValidGroup()
    {
        final GroupAccount account = GroupAccount.create( "enonic:group1" );
        final CreateAccount command = new CreateAccount();

        command.account( account );
        assertSame( account, command.getAccount() );

        command.validate();
    }

    @Test
    public void testValidRole()
    {
        final RoleAccount account = RoleAccount.create( "system:contributors" );
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
