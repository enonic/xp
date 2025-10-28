package com.enonic.xp.lib.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;

class GetOutboundDependenciesHandlerTest
    extends BaseContentHandlerTest
{

    private static final ContentIds CONTENT_IDS =
        ContentIds.from( "d898972d-f1eb-40a8-a7f2-16abd4c105da", "9efadb7b-bb14-4c74-82ec-cec95069d0c2" );

    @BeforeEach
    void setUp()
    {
        final Content content = Mockito.mock( Content.class );

        Mockito.when( content.getId() ).thenReturn( ContentId.from( "contentId" ) );
        Mockito.when( contentService.getByPath( Mockito.any( ContentPath.class ) ) ).thenReturn( content );
    }

    @Test
    void testExample()
    {
        Mockito.when( this.contentService.getOutboundDependencies( Mockito.any( ContentId.class ) ) ).thenReturn( CONTENT_IDS );

        runScript( "/lib/xp/examples/content/getOutboundDependencies.js" );
    }

    @Test
    void testGetOutboundDependencies_ById()
    {
        Mockito.when( contentService.getOutboundDependencies( Mockito.any( ContentId.class ) ) ).thenReturn( CONTENT_IDS );

        runFunction( "/test/GetOutboundDependenciesHandlerTest.js", "getById" );
    }

    @Test
    void testGetOutboundDependencies_ByPath()
    {
        Mockito.when( contentService.getOutboundDependencies( Mockito.any( ContentId.class ) ) ).thenReturn( CONTENT_IDS );

        runFunction( "/test/GetOutboundDependenciesHandlerTest.js", "getByPath" );
    }

}
