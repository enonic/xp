package com.enonic.xp.portal.impl.jslib.url;

import org.junit.Test;

import com.enonic.xp.portal.impl.jslib.content.url.AbstractUrlHandler;
import com.enonic.xp.portal.impl.jslib.content.url.ComponentUrlHandler;

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
