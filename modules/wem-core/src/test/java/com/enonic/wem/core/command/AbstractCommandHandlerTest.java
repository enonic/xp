package com.enonic.wem.core.command;

import javax.jcr.Session;

import org.mockito.Mockito;

import com.enonic.wem.api.Client;

public abstract class AbstractCommandHandlerTest
{
    protected CommandContext context;

    protected Session session;

    protected Client client;

    public void initialize()
        throws Exception
    {
        // TODO: Will wire in JCR in-memory here
        this.context = new CommandContext();
        this.context.setClient( client );
        session = Mockito.mock( Session.class );
        this.context.setJcrSession( session );
    }
}
