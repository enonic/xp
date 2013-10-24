package com.enonic.wem.core.command;

import javax.inject.Inject;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;


public final class CommandContextFactoryImpl
    implements CommandContextFactory
{
    private JcrSessionProvider jcrSessionProvider;

    @Override
    public CommandContext create()
    {
        try
        {
            return doCreate();
        }
        catch ( final RuntimeException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new SystemException( e, e.getMessage() );
        }
    }

    private CommandContext doCreate()
        throws Exception
    {
        final CommandContext context = new CommandContext();
        context.setJcrSession( this.jcrSessionProvider.login() );
        return context;
    }

    @Inject
    public void setJcrSessionProvider( final JcrSessionProvider jcrSessionProvider )
    {
        this.jcrSessionProvider = jcrSessionProvider;
    }
}
