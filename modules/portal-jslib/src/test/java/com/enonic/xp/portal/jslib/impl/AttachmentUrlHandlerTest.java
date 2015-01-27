package com.enonic.xp.portal.jslib.impl;

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
