package com.enonic.wem.jsapi.internal.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.jsapi.internal.AbstractHandlerTest;
import com.enonic.wem.script.command.CommandHandler;

public class GetChildContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final GetChildContentHandler handler = new GetChildContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void getById()
        throws Exception
    {
        // final Content content = ContentFixtures.newContent();
        // Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        execute( "getChildrenById" );
    }

    @Test
    public void getByPath()
        throws Exception
    {
        // final Content content = ContentFixtures.newContent();
        // Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        execute( "getChildrenByPath" );
    }
}
