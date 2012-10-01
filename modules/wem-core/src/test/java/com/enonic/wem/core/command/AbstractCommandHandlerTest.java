package com.enonic.wem.core.command;

import javax.jcr.Session;

import org.mockito.Mockito;

public abstract class AbstractCommandHandlerTest
{
    protected CommandContext context;

    protected Session session;

    public void initialize()
        throws Exception
    {
        // TODO: Will wire in JCR in-memory here
        this.context = new CommandContext();
        session = Mockito.mock( Session.class );
        this.context.setJcrSession( session );
    }
}
