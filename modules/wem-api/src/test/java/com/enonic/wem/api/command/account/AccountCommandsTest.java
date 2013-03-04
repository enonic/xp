package com.enonic.wem.api.command.account;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountCommandsTest
{
    @Test
    public void testChangePassword()
    {
        final ChangePassword command = new AccountCommands().changePassword();
        assertNotNull( command );
    }

    @Test
    public void testCreateAccount()
    {
        final CreateAccount command = new AccountCommands().create();
        assertNotNull( command );
    }

    @Test
    public void testDeleteAccount()
    {
        final DeleteAccount command = new AccountCommands().delete();
        assertNotNull( command );
    }

    @Test
    public void testFindAccounts()
    {
        final FindAccounts command = new AccountCommands().find();
        assertNotNull( command );
    }

    @Test
    public void testFindMemberships()
    {
        final FindMemberships command = new AccountCommands().findMemberships();
        assertNotNull( command );
    }

    @Test
    public void testFindMembers()
    {
        final FindMembers command = new AccountCommands().findMembers();
        assertNotNull( command );
    }

    @Test
    public void testUpdateAccounts()
    {
        final UpdateAccounts command = new AccountCommands().update();
        assertNotNull( command );
    }

    @Test
    public void testValidatePassword()
    {
        final ValidatePassword command = new AccountCommands().validatePassword();
        assertNotNull( command );
    }
}
