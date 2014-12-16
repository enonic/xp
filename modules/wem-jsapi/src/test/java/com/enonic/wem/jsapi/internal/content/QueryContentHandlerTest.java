package com.enonic.wem.jsapi.internal.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.jsapi.internal.AbstractHandlerTest;
import com.enonic.wem.script.command.CommandHandler;

public class QueryContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final QueryContentHandler handler = new QueryContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void query()
        throws Exception
    {
        // final Content content = ContentFixtures.newContent();
        // Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        execute( "query" );
    }

    @Test
    public void queryEmpty()
        throws Exception
    {
        // final Content content = ContentFixtures.newContent();
        // Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        execute( "queryEmpty" );
    }
}
