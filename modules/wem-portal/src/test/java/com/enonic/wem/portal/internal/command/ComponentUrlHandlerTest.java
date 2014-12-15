package com.enonic.wem.portal.internal.command;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;

public class ComponentUrlHandlerTest
    extends AbstractUrlHandlerTest
{
    @Override
    protected CommandHandler createHandler()
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
