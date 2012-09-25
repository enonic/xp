package com.enonic.wem.core.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;

@Component
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
        catch ( final BaseException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new SystemException( e, e.getMessage() );
        }
    }

    protected CommandContext doCreate()
        throws Exception
    {
        final CommandContext context = new CommandContext();
        context.setJcrSession( this.jcrSessionProvider.login() );
        return context;
    }

    @Autowired
    public void setJcrSessionProvider( final JcrSessionProvider jcrSessionProvider )
    {
        this.jcrSessionProvider = jcrSessionProvider;
    }
}
