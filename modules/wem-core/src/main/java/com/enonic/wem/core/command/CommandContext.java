package com.enonic.wem.core.command;

import javax.jcr.Session;

import com.enonic.wem.api.Client;

public final class CommandContext
{
    private Client client;

    private Session jcrSession;

    public Client getClient()
    {
        return client;
    }

    public void setClient( final Client client )
    {
        this.client = client;
    }

    public Session getJcrSession()
    {
        return jcrSession;
    }

    public void setJcrSession( final Session jcrSession )
    {
        this.jcrSession = jcrSession;
    }

    public void dispose()
    {
        if ( this.jcrSession != null )
        {
            this.jcrSession.logout();
        }
    }
}
