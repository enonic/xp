package com.enonic.xp.portal.impl.jslib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.xp.portal.impl.jslib.ContentFixtures;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.impl.jslib.AbstractHandlerTest;

public class DeleteContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final DeleteContentHandler handler = new DeleteContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void deleteById()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( content );

        execute( "deleteById" );
    }

    @Test
    public void deleteByPath()
        throws Exception
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( content );

        execute( "deleteByPath" );
    }

    @Test
    public void deleteById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( Mockito.any() ) ).thenThrow( new ContentNotFoundException( id, null ) );

        execute( "deleteById_notFound" );
    }

    @Test
    public void deleteByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b" );
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenThrow( new ContentNotFoundException( path, null ) );

        execute( "deleteByPath_notFound" );
    }
}
