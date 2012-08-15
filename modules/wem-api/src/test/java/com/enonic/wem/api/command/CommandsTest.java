package com.enonic.wem.api.command;

import org.junit.Test;

import com.enonic.wem.api.command.account.AccountCommands;

import static org.junit.Assert.*;

public class CommandsTest
{
    @Test
    public void testAccount()
    {
        final AccountCommands commands = Commands.account();
        assertNotNull( commands );
    }
}
