package com.enonic.xp.lib.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetChildContentHandlerTest
    extends ScriptTestSupport
{
    private ContentService contentService;

    @Before
    public void setup()
    {
        this.contentService = Mockito.mock( ContentService.class );
        addService( ContentService.class, this.contentService );
    }

    @Test
    public void getChildrenById()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents();

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runTestFunction( "/test/GetChildContentHandlerTest.js", "getChildrenById" );
    }

    @Test
    public void getChildrenByPath()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents();

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runTestFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath" );
    }

    @Test
    public void getChildrenById_notFound()
        throws Exception
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runTestFunction( "/test/GetChildContentHandlerTest.js", "getChildrenById_notFound" );
    }

    @Test
    public void getChildrenByPath_notFound()
        throws Exception
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        runTestFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath_notFound" );
    }

    @Test
    public void getChildrenByPath_allParameters()
        throws Exception
    {
        final Contents contents = TestDataFixtures.newContents();

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();

        final FindContentByParentParams expectedFindParams = FindContentByParentParams.create().
            parentPath( ContentPath.from( "/a/b/mycontent" ) ).
            from( 5 ).
            size( 3 ).
            childOrder( ChildOrder.from( "_modifiedTime ASC" ) ).
            build();
        Mockito.when( this.contentService.findByParent( Mockito.eq( expectedFindParams ) ) ).thenReturn( findResult );

        runTestFunction( "/test/GetChildContentHandlerTest.js", "getChildrenByPath_allParameters" );
    }
}
