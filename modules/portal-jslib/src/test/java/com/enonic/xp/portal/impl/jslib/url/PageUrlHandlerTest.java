package com.enonic.xp.portal.impl.jslib.url;

import org.junit.Test;

import com.enonic.xp.portal.impl.jslib.content.url.AbstractUrlHandler;
import com.enonic.xp.portal.impl.jslib.content.url.PageUrlHandler;

public class PageUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected AbstractUrlHandler createUrlHandler()
    {
        return new PageUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
