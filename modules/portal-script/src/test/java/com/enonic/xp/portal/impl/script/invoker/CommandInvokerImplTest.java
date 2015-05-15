package com.enonic.xp.portal.impl.script.invoker;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandInvokerImplTest
{
    private CommandInvokerImpl invoker;

    @Before
    public void setup()
    {
        this.invoker = new CommandInvokerImpl();
    }

    @Test
    public void testInvoke()
    {
        final TestCommandHandler handler = new TestCommandHandler();
        this.invoker.addHandler( handler );

        final CommandRequestImpl request = new CommandRequestImpl();
        request.setName( "test" );

        final Object result = this.invoker.invoke( request );
        assertNotNull( result );
        assertEquals( "test", result );

        this.invoker.removeHandler( handler );

        try
        {
            this.invoker.invoke( request );
            fail( "Should throw exception" );
        }
        catch ( final IllegalArgumentException e )
        {
            // Do nothing
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoke_notFound()
    {
        final CommandRequestImpl request = new CommandRequestImpl();
        request.setName( "test" );

        this.invoker.invoke( request );
    }
}
