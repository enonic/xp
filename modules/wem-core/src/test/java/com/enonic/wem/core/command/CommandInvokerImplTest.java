package com.enonic.wem.core.command;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.inject.Provider;

import com.enonic.wem.api.exception.SystemException;

import static org.junit.Assert.*;

public class CommandInvokerImplTest
{
    private Map<Class, Provider<CommandHandler>> handlerMap;

    private CommandInvokerImpl invoker;

    private CommandContext context;

    @Before
    public void setup()
    {
        this.handlerMap = Maps.newHashMap();
        this.invoker = new CommandInvokerImpl( this.handlerMap );
        this.context = new CommandContext();
    }

    private TestCommandHandler addHandler()
    {
        final TestCommandHandler handler = new TestCommandHandler();
        this.handlerMap.put( TestCommand.class, new Provider<CommandHandler>()
        {
            @Override
            public CommandHandler get()
            {
                return handler;
            }
        } );

        return handler;
    }

    @Test(expected = SystemException.class)
    public void invoke_noSuchCommand()
    {
        final TestCommand command = new TestCommand();
        this.invoker.invoke( this.context, command );
        fail( "Should fail" );
    }

    @Test
    public void invoke_success()
    {
        final TestCommandHandler handler = addHandler();

        final TestCommand command = new TestCommand();
        this.invoker.invoke( this.context, command );

        assertEquals( 1, handler.executeCount );
        assertEquals( "ok", command.getResult() );
    }

    @Test
    public void invoke_runtimeException_noWrap()
    {
        final TestCommandHandler handler = addHandler();
        handler.errorOnHandle = new RuntimeException( "error" );

        final TestCommand command = new TestCommand();

        try
        {
            this.invoker.invoke( this.context, command );
        }
        catch ( final Exception e )
        {
            assertEquals( 1, handler.executeCount );
            assertSame( handler.errorOnHandle, e );
            assertNull( e.getCause() );
        }
    }

    @Test
    public void invoke_exception_wrap()
    {
        final TestCommandHandler handler = addHandler();
        handler.errorOnHandle = new Exception( "error" );

        final TestCommand command = new TestCommand();

        try
        {
            this.invoker.invoke( this.context, command );
        }
        catch ( final Exception e )
        {
            assertEquals( 1, handler.executeCount );
            assertSame( SystemException.class, e.getClass() );
            assertSame( handler.errorOnHandle, e.getCause() );
        }
    }
}
