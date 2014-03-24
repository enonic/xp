package com.enonic.wem.core.command;

import javax.jcr.Session;

import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.entity.NodeService;

public abstract class AbstractCommandHandlerTest
{
    protected CommandContext context;

    protected Session session;

    protected Client client;

    protected NodeService nodeService;

    public void initialize()
        throws Exception
    {
        this.client = Mockito.mock( Client.class );
        this.nodeService = Mockito.mock( NodeService.class );

        // TODO: Will wire in JCR in-memory here
        this.context = new CommandContext();
        this.context.setClient( client );
        session = Mockito.mock( Session.class );
        this.context.setJcrSession( session );
    }

    @After
    public void afterAbstractCommandHandlerTest()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }
}
