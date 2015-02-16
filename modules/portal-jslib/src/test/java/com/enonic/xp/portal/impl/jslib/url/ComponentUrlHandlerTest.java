package com.enonic.xp.portal.impl.jslib.url;

import org.junit.Test;

public class ComponentUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected AbstractUrlHandler createUrlHandler()
    {
        return new ComponentUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
