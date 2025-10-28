package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentsResult;

class DeleteContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( DeleteContentsResult.create().build() );

        runScript( "/lib/xp/examples/content/delete.js" );
    }

    @Test
    void deleteById()
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( DeleteContentsResult.create().build() );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteById" );
    }

    @Test
    void deleteByPath()
    {
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenReturn( DeleteContentsResult.create().build() );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteByPath" );
    }

    @Test
    void deleteById_notFound()
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( Mockito.any() ) )
            .thenThrow( ContentNotFoundException.class );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteById_notFound" );
    }

    @Test
    void deleteByPath_notFound()
    {
        final ContentPath path = ContentPath.from( "/a/b" );
        Mockito.when( this.contentService.delete( Mockito.any() ) ).thenThrow( ContentNotFoundException.class );

        runFunction( "/test/DeleteContentHandlerTest.js", "deleteByPath_notFound" );
    }
}
