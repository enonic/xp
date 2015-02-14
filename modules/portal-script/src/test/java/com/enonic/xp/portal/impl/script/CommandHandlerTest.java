package com.enonic.xp.portal.impl.script;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;

import static org.junit.Assert.*;

public class CommandHandlerTest
    extends AbstractScriptTest
{
    private TestCommandHandler handler;

    @Before
    public void setUp()
    {
        this.handler = new TestCommandHandler();
        addHandler( this.handler );
    }

    @Test
    public void commandNotFound()
    {
        removeHandler( this.handler );
        final ResourceKey script = ResourceKey.from( "mymodule:/command/command-test.js" );

        try
        {
            runTestScript( script );
            fail( "Should throw ResourceProblemException" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( 1, e.getLineNumber() );
            assertEquals( script, e.getResource() );
            assertEquals( "Command [test.command] not found", e.getMessage() );
        }
    }

    @Test
    public void executeCommand()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/command/command-test.js" );
        runTestScript( script );
    }
}
