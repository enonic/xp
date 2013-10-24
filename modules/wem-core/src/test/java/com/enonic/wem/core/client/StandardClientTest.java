package com.enonic.wem.core.client;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandContextFactory;
import com.enonic.wem.core.command.CommandInvoker;

import static org.junit.Assert.*;

public class StandardClientTest
{
    private StandardClient client;

    private Command<Object> command;

    private CommandInvoker invoker;

    private CommandContext context;

    @Before
    public void setup()
    {
        createCommandMock();
        this.invoker = Mockito.mock( CommandInvoker.class );

        this.client = new StandardClient();
        this.client.setInvoker( this.invoker );

        this.context = new CommandContext();
        final CommandContextFactory factory = Mockito.mock( CommandContextFactory.class );
        Mockito.when( factory.create() ).thenReturn( this.context );
        this.client.setCommandContextFactory( factory );
    }

    @SuppressWarnings("unchecked")
    private void createCommandMock()
    {
        this.command = Mockito.mock( Command.class );
    }

    @Test
    public void testExecute()
    {
        final Object result = new Object();
        this.command.setResult( result );

        assertSame( result, this.client.execute( this.command ) );

        Mockito.verify( this.command, Mockito.times( 1 ) ).validate();
        Mockito.verify( this.invoker, Mockito.times( 1 ) ).invoke( this.context, this.command );
    }
}
