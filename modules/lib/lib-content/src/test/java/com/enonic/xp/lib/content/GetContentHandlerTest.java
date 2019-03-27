package com.enonic.xp.lib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;

public class GetContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runScript( "/lib/xp/examples/content/get.js" );
    }

    @Test
    public void getById()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getById" );
    }

    @Test
    public void getByIdWithPageAsFragment()
        throws Exception
    {
        final Content content = TestDataFixtures.newContentWithPageAsFragment();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getByIdWithPageAsFragment" );
    }

    @Test
    public void getByPath()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getByPath" );
    }

    @Test
    public void getById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( id ) ).thenThrow( new ContentNotFoundException( id, null ) );

        runFunction( "/test/GetContentHandlerTest.js", "getById_notFound" );
    }

    @Test
    public void getByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( new ContentNotFoundException( path, null ) );

        runFunction( "/test/GetContentHandlerTest.js", "getByPath_notFound" );
    }
}
