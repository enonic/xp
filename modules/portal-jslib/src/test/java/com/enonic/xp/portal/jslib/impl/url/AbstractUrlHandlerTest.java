package com.enonic.xp.portal.jslib.impl.url;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.jslib.impl.AbstractHandlerTest;
import com.enonic.xp.portal.url.PortalUrlService;

public abstract class AbstractUrlHandlerTest
    extends AbstractHandlerTest
{
    protected final CommandHandler createHandler()
        throws Exception
    {
        final AbstractUrlHandler handler = createUrlHandler();
        handler.setUrlService( Mockito.mock( PortalUrlService.class, (Answer) this::urlAnswer ) );
        return handler;
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        return invocation.getArguments()[0].toString();
    }

    protected abstract AbstractUrlHandler createUrlHandler()
        throws Exception;
}
