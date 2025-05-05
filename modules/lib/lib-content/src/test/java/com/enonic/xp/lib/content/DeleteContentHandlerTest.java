package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentsResult;

public class DeleteContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( DeleteContentsResult.create().build() );

        runScript( "/lib/xp/examples/content/delete.js" );
    }

    @Test
    public void deleteById()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( DeleteContentsResult.create().build() );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteById" );
    }

    @Test
    public void deleteByPath()
        throws Exception
    {
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( DeleteContentsResult.create().build() );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteByPath" );
    }

    @Test
    public void deleteById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( Mockito.any() ) )
            .thenThrow( ContentNotFoundException.class );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteById_notFound" );
    }

    @Test
    public void deleteByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b" );
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenThrow( ContentNotFoundException.class );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteByPath_notFound" );
    }
}
