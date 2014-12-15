package com.enonic.wem.portal.internal.command;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;

public class PageUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected CommandHandler createHandler()
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
