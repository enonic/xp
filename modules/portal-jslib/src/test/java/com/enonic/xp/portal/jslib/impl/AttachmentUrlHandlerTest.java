package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;

public class AttachmentUrlHandlerTest
    extends AbstractHandlerTest
{
    @Override
    protected CommandHandler createHandler()
    {
        return new AttachmentUrlHandler();
    }

    @Test
    public void createUrl_with_name()
        throws Exception
    {
        execute( "createUrl_with_name" );
    }

    @Test
    public void createUrl_with_label()
        throws Exception
    {
        execute( "createUrl_with_label" );
    }

    @Test
    public void createUrl_with_id_and_name()
        throws Exception
    {
        execute( "createUrl_with_id_and_name" );
    }

    @Test
    public void createUrl_with_id_and_label()
        throws Exception
    {
        execute( "createUrl_with_id_and_label" );
    }

}
