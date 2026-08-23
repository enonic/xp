package com.enonic.xp.lib.content;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ReorderChildContentParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.index.ChildOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SortContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = newContentWithOrderKey();
        when( contentService.getByPath( ContentPath.from( "/my-site/my-list" ) ) ).thenReturn( content );
        when( contentService.sort( any() ) ).thenReturn( SortContentResult.create()
                                                             .content( content )
                                                             .movedChildren(
                                                                 ContentIds.from( "b7fd8fa8-5bda-4933-a04c-a3b46bccc4fa" ) )
                                                             .build() );

        runScript( "/lib/xp/examples/content/sort.js" );
    }

    @Test
    void flipToManual()
    {
        final Content content = newContentWithOrderKey();
        when( contentService.getByPath( ContentPath.from( "/my-site/my-list" ) ) ).thenReturn( content );
        when( contentService.sort( any() ) ).thenReturn(
            SortContentResult.create().content( content ).movedChildren( ContentIds.empty() ).build() );

        runFunction( "/test/SortContentHandlerTest.js", "flipToManual" );

        final SortContentParams params = captureSortParams();
        assertEquals( content.getId(), params.getContentId() );
        assertEquals( ChildOrder.orderKeyOrder(), params.getChildOrder() );
        assertTrue( params.getReorderChildContents().isEmpty() );
    }

    @Test
    void sortByExpression()
    {
        final Content content = newContentWithOrderKey();
        when( contentService.sort( any() ) ).thenReturn(
            SortContentResult.create().content( content ).movedChildren( ContentIds.empty() ).build() );

        runFunction( "/test/SortContentHandlerTest.js", "sortByExpression" );

        final SortContentParams params = captureSortParams();
        assertEquals( ChildOrder.from( "displayName ASC" ), params.getChildOrder() );
        assertTrue( params.getReorderChildContents().isEmpty() );
    }

    @Test
    void reorderWithAnchors()
    {
        final Content content = newContentWithOrderKey();
        when( contentService.sort( any() ) ).thenReturn( SortContentResult.create()
                                                             .content( content )
                                                             .movedChildren( ContentIds.from( "child-1", "child-2" ) )
                                                             .build() );

        runFunction( "/test/SortContentHandlerTest.js", "reorderWithAnchors" );

        final SortContentParams params = captureSortParams();
        assertEquals( ChildOrder.orderKeyOrder(), params.getChildOrder() );

        final List<ReorderChildContentParams> reorder = params.getReorderChildContents();
        assertEquals( 2, reorder.size() );
        assertEquals( "child-1", reorder.get( 0 ).getContentToMove().toString() );
        assertEquals( "3a00000zzzz.above", reorder.get( 0 ).getAfterOrderKey() );
        assertEquals( "3a00001zzzz.below", reorder.get( 0 ).getBeforeOrderKey() );
        assertEquals( "child-2", reorder.get( 1 ).getContentToMove().toString() );
        assertEquals( "3a00001zzzz.below", reorder.get( 1 ).getAfterOrderKey() );
        assertNull( reorder.get( 1 ).getBeforeOrderKey() );
    }

    @Test
    void missingParams()
    {
        runFunction( "/test/SortContentHandlerTest.js", "missingParams" );

        verify( contentService, never() ).sort( any() );
    }

    private SortContentParams captureSortParams()
    {
        final ArgumentCaptor<SortContentParams> captor = ArgumentCaptor.forClass( SortContentParams.class );
        verify( contentService ).sort( captor.capture() );
        return captor.getValue();
    }

    private static Content newContentWithOrderKey()
    {
        return Content.create( TestDataFixtures.newContent() ).orderKey( "3ala4x8dnbc.123456" ).build();
    }
}
