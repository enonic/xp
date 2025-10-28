package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;

class GetAttachmentsHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runScript( "/lib/xp/examples/content/getAttachments.js" );
    }

    @Test
    void getById()
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runFunction( "/test/GetAttachmentsHandlerTest.js", "getById" );
    }

    @Test
    void getByPath()
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        runFunction( "/test/GetAttachmentsHandlerTest.js", "getByPath" );
    }

    @Test
    void getById_notFound()
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( Mockito.any() ) )
            .thenThrow( ContentNotFoundException.class );

        runFunction( "/test/GetAttachmentsHandlerTest.js", "getById_notFound" );
    }

    @Test
    void getByPath_notFound()
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( ContentNotFoundException.class );

        runFunction( "/test/GetAttachmentsHandlerTest.js", "getByPath_notFound" );
    }
}
