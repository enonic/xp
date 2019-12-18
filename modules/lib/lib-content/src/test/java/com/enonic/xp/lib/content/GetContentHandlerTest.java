package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.context.ContextAccessor;

public class GetContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPathAndVersionId( Mockito.any( ContentPath.class ),
                                                                 Mockito.any( ContentVersionId.class ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getByIdAndVersionId( Mockito.any( ContentId.class ),
                                                               Mockito.any( ContentVersionId.class ) ) ).thenReturn(content );

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

    @Test
    public void getByIdAndVersionId()
    {
        final Content content = TestDataFixtures.newExampleContent();

        final ContentId contentId = ContentId.from( "mycontentId" );
        final ContentVersionId versionId = ContentVersionId.from( "versionId" );
        Mockito.when( this.contentService.getByIdAndVersionId( contentId, versionId ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getByIdAndVersionId" );
    }

    @Test
    public void getByIdAndVersionId_NotFound()
    {
        final ContentId contentId = ContentId.from( "mycontentId" );
        final ContentVersionId versionId = ContentVersionId.from( "versionId" );
        Mockito.when( this.contentService.getByIdAndVersionId( contentId, versionId ) ).thenThrow(
            new ContentNotFoundException( contentId, versionId, ContextAccessor.current().getBranch() ) );

        runFunction( "/test/GetContentHandlerTest.js", "getByIdAndVersionId_notFound" );
    }

    @Test
    public void getByPathAndVersionId()
    {
        final Content content = TestDataFixtures.newExampleContent();

        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        final ContentVersionId versionId = ContentVersionId.from( "versionId" );

        Mockito.when( this.contentService.getByPathAndVersionId( path, versionId ) ).thenReturn( content );

        runFunction( "/test/GetContentHandlerTest.js", "getByPathAndVersionId" );
    }

    @Test
    public void getByPathAndVersionId_NotFound()
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        final ContentVersionId versionId = ContentVersionId.from( "versionId" );

        Mockito.when( this.contentService.getByPathAndVersionId( path, versionId ) ).thenThrow(
            new ContentNotFoundException( path, versionId, ContextAccessor.current().getBranch() ) );

        runFunction( "/test/GetContentHandlerTest.js", "getByPathAndVersionId_notFound" );
    }

}
