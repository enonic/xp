package com.enonic.wem.portal.internal.command;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;

public class ServiceUrlHandlerTest
    extends AbstractUrlHandlerTest
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
