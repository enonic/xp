package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.index.ChildOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
    {
        final Contents contents = TestDataFixtures.newContents( 3 );

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenById" );
    }

    @Test
    public void getChildrenByPath()
    {
        final Contents contents = TestDataFixtures.newContents( 3 );

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath" );
    }

    @Test
    public void getChildrenById_notFound()
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenById_notFound" );
    }

    @Test
    public void getChildrenByPath_notFound()
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath_notFound" );
    }

    @Test
    public void getChildrenByPath_allParameters()
    {
        final Contents contents = TestDataFixtures.newContents( 3 );

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();

        Mockito.when( this.contentService.findByParent( any() ) ).thenReturn( findResult );

        runFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath_allParameters" );
        verify( this.contentService ).findByParent( Mockito.argThat( params -> {
            assertEquals( ContentPath.from( "/a/b/mycontent" ), params.getParentPath() );
            assertEquals( 5, params.getFrom() );
            assertEquals( 3, params.getSize() );
            assertEquals( ChildOrder.from( "_modifiedTime ASC" ), params.getChildOrder() );
            return true;
        } ) );
    }
}
