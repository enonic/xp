package com.enonic.wem.api.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandTest
{
    @Test
    public void testResult()
    {
        final Command<Boolean> command = new Command<Boolean>()
        {
            @Override
            public void validate()
            {
            }
        };

        assertNull( command.getResult() );
        command.setResult( true );
        assertTrue( command.getResult() );
    }
}
