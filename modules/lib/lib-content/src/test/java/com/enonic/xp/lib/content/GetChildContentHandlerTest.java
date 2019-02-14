package com.enonic.xp.lib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.index.ChildOrder;

public class GetChildContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final Contents contents = TestDataFixtures.newContents( 2 );

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runScript( "/lib/xp/examples/content/getChildren.js" );
    }

    @Test
    public void getChildrenById()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents( 3 );

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenById" );
    }

    @Test
    public void getChildrenByPath()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents( 3 );

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath" );
    }

    @Test
    public void getChildrenById_notFound()
        throws Exception
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenById_notFound" );
    }

    @Test
    public void getChildrenByPath_notFound()
        throws Exception
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath_notFound" );
    }

    @Test
    public void getChildrenByPath_allParameters()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents( 3 );

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();

        final FindContentByParentParams expectedFindParams = FindContentByParentParams.create().
            parentPath( ContentPath.from( "/a/b/mycontent" ) ).
            from( 5 ).
            size( 3 ).
            childOrder( ChildOrder.from( "_modifiedTime ASC" ) ).
            build();
        Mockito.when( this.contentService.findByParent( Mockito.eq( expectedFindParams ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath_allParameters" );
    }
}
