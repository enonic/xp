package com.enonic.xp.lib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;

public class GetContentsHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final Contents contents = TestDataFixtures.newContents( 2 );

        Mockito.when( this.contentService.getByPaths( Mockito.any() ) ).thenReturn( Contents.from( contents.first() ) );
        Mockito.when( this.contentService.getByIds( Mockito.any() ) ).thenReturn( contents );

        runScript( "/site/lib/xp/examples/content/getContents.js" );

    }

    @Test
    public void getByIds()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents( 2 );
        Mockito.when( this.contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );

        runFunction( "/site/test/GetContentsHandlerTest.js", "getByIds" );
    }

    @Test
    public void getByPaths()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents( 2 );
        Mockito.when( this.contentService.getByPaths( Mockito.any( ContentPaths.class ) ) ).thenReturn( contents );

        runFunction( "/site/test/GetContentsHandlerTest.js", "getByPaths" );
    }

    @Test
    public void getByIds_notFound()
        throws Exception
    {
        final ContentIds ids = ContentIds.from( ContentId.from( "654321" ), ContentId.from( "123456" ) );
        final ContentPaths paths = ContentPaths.from( "/a/b/mycontent" );

        Mockito.when( this.contentService.getByIds( new GetContentByIdsParams( ids ) ) ).thenThrow(
            new ContentNotFoundException( ids.first(), null ) );
        Mockito.when( this.contentService.getByIds( new GetContentByIdsParams( ContentIds.from( "123456" ) ) ) ).thenReturn(
            TestDataFixtures.newContents( 1 ) );
        Mockito.when( this.contentService.getByPaths( paths ) ).thenReturn( TestDataFixtures.newContents( 1 ) );

        runFunction( "/site/test/GetContentsHandlerTest.js", "getByIds_notFound" );
    }

    @Test
    public void getByPaths_notFound()
        throws Exception
    {
        final ContentIds ids = ContentIds.from( ContentId.from( "123456" ) );
        final ContentPaths paths = ContentPaths.from( "/c/d/othercontent", "/a/b/mycontent" );

        Mockito.when( this.contentService.getByPaths( paths ) ).thenThrow( new ContentNotFoundException( paths.first(), null ) );
        Mockito.when( this.contentService.getByPaths( ContentPaths.from( "/a/b/mycontent" ) ) ).thenReturn(
            TestDataFixtures.newContents( 1 ) );
        Mockito.when( this.contentService.getByIds( new GetContentByIdsParams( ids ) ) ).thenReturn( TestDataFixtures.newContents( 1 ) );

        runFunction( "/site/test/GetContentsHandlerTest.js", "getByPaths_notFound" );
    }
}
