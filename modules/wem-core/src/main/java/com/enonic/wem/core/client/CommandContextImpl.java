package com.enonic.wem.core.client;

import com.enonic.wem.api.Client;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.jcr.JcrSession;

final class CommandContextImpl
    implements CommandContext
{
    private Client client;

    private JcrSession jcrSession;

    @Override
    public Client getClient()
    {
        return client;
    }

    public void setClient( final Client client )
    {
        this.client = client;
    }

    @Override
    public JcrSession getJcrSession()
    {
        return jcrSession;
    }

    public void setJcrSession( final JcrSession jcrSession )
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
