package com.enonic.xp.portal.impl.jslib.url;

import org.junit.Test;

public class AssetUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected AbstractUrlHandler createUrlHandler()
    {
        return new AssetUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
