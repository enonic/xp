package com.enonic.xp.portal.jslib.impl.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.xp.portal.jslib.impl.ContentFixtures;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.jslib.impl.AbstractHandlerTest;

public class GetChildContentHandlerTest
    extends AbstractHandlerTest
{
    private ContentService contentService;

    @Override
    protected CommandHandler createHandler()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );

        final GetChildContentHandler handler = new GetChildContentHandler();
        handler.setContentService( this.contentService );

        return handler;
    }

    @Test
    public void getChildrenById()
        throws Exception
    {
        final Contents contents = ContentFixtures.newContents();

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        execute( "getChildrenById" );
    }

    @Test
    public void getChildrenByPath()
        throws Exception
    {
        final Contents contents = ContentFixtures.newContents();

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        execute( "getChildrenByPath" );
    }

    @Test
    public void getChildrenById_notFound()
        throws Exception
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        execute( "getChildrenById_notFound" );
    }

    @Test
    public void getChildrenByPath_notFound()
        throws Exception
    {
        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( 0 ).totalHits( 0 ).contents( Contents.empty() ).build();
        Mockito.when( this.contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( findResult );

        execute( "getChildrenByPath_notFound" );
    }

    @Test
    public void getChildrenByPath_allParameters()
        throws Exception
    {
        final Contents contents = ContentFixtures.newContents();

        final FindContentByParentResult findResult =
            FindContentByParentResult.create().hits( contents.getSize() ).totalHits( 20 ).contents( contents ).build();

        final FindContentByParentParams expectedFindParams = FindContentByParentParams.create().
            parentPath( ContentPath.from( "/a/b/mycontent" ) ).
            from( 5 ).
            size( 3 ).
            childOrder( ChildOrder.from( "_modifiedTime ASC" ) ).
            build();
        Mockito.when( this.contentService.findByParent( Mockito.eq( expectedFindParams ) ) ).thenReturn( findResult );

        execute( "getChildrenByPath_allParameters" );
    }

}
