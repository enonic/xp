package com.enonic.xp.portal.impl.jslib.current;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;
import com.enonic.xp.portal.impl.jslib.ContentFixtures;
import com.enonic.xp.portal.script.command.CommandHandler;

public class GetCurrentContentHandlerTest
    extends AbstractHandlerTest
{

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        return new GetCurrentContentHandler();
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
