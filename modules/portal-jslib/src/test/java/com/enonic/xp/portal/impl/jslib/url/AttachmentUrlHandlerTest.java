package com.enonic.xp.portal.impl.jslib.url;

import org.junit.Test;

public class AttachmentUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected AbstractUrlHandler createUrlHandler()
    {
        return new AttachmentUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
