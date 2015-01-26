package com.enonic.xp.portal.jslib.impl;

import com.enonic.wem.script.command.CommandHandler;
import com.enonic.xp.portal.jslib.AbstractHandlerTest;

public abstract class AbstractUrlHandlerTest
    extends AbstractHandlerTest
{
    protected final CommandHandler createHandler()
        throws Exception
    {
        final AbstractUrlHandler handler = createUrlHandler();
        handler.setUrlService( new MockPortalUrlService() );
        return handler;
    }

    protected abstract AbstractUrlHandler createUrlHandler()
        throws Exception;
}
