package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;
import org.mockito.Mockito;

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
        Mockito.when( context.getContent() ).thenReturn( content );

        execute( "getContent" );
    }

    @Test
    public void getContent_notFound()
        throws Exception
    {
        Mockito.when( context.getContent() ).thenReturn( null );

        execute( "getContent_notFound" );
    }

}
