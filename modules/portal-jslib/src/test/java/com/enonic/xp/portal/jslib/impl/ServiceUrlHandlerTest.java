package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;

public class ServiceUrlHandlerTest
    extends AbstractHandlerTest
{
    @Override
    protected CommandHandler createHandler()
    {
        return new ServiceUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
