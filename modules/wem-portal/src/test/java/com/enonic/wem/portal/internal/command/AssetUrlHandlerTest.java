package com.enonic.wem.portal.internal.command;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;

public class AssetUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected CommandHandler createHandler()
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
