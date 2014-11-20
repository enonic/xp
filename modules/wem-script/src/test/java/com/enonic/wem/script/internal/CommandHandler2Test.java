package com.enonic.wem.script.internal;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.script.AbstractScriptTest;

import static org.junit.Assert.*;

public class CommandHandler2Test
    extends AbstractScriptTest
{
    private TestCommandHandler2 handler;

    @Before
    public void setUp()
    {
        this.handler = new TestCommandHandler2();
        addHandler( this.handler );
    }

    @Test
    public void commandNotFound()
    {
        removeHandler( this.handler );
        final ResourceKey script = ResourceKey.from( "mymodule:/command/command-v2-test.js" );

        try
        {
            runTestScript( script );
            fail( "Should throw ResourceProblemException" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( 3, e.getLineNumber() );
            assertEquals( script, e.getResource() );
            assertEquals( "Command [test.command] not found", e.getMessage() );
        }
    }

    @Test
    public void executeCommand()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/command/command-v2-test.js" );
        runTestScript( script );
    }
}
