package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.mapper.ContentFixtures;

public class GetContentHandlerTest
    extends AbstractHandlerTest
{

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        return new GetContentHandler();
    }

    @Test
    public void getContent()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        context.setContent( content );

        execute( "getContent" );
    }

    @Test
    public void getContent_notFound()
        throws Exception
    {
        context.setContent( null );
        execute( "getContent_notFound" );
    }

}
