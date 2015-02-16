package com.enonic.xp.portal.impl.jslib.url;

import org.junit.Test;

public class ImageUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected AbstractUrlHandler createUrlHandler()
    {
        return new ImageUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
