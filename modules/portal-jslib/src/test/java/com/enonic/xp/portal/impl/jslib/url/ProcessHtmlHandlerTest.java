package com.enonic.xp.portal.impl.jslib.url;

import org.junit.Test;

public class ProcessHtmlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected AbstractUrlHandler createUrlHandler()
    {
        return new ProcessHtmlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
