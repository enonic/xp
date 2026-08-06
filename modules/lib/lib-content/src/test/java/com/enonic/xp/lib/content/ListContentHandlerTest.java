package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentListEntry;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ListContentsByParentParams;
import com.enonic.xp.content.ListContentsByParentResult;

class ListContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        Mockito.when( contentService.list( Mockito.isA( ListContentsByParentParams.class ) ) ).thenReturn(
            ListContentsByParentResult.create()
                .addEntry( ContentListEntry.create().id( ContentId.from( "id1" ) ).path( ContentPath.from( "/path/to/a" ) ).build() )
                .addEntry( ContentListEntry.create().id( ContentId.from( "id2" ) ).path( ContentPath.from( "/path/to/b" ) ).build() )
                .build() );

        runScript( "/lib/xp/examples/content/list.js" );

        final ListContentsByParentParams params = capturedParams();
        Assertions.assertEquals( ContentPath.from( "/path/to" ), params.getParentPath() );
        Assertions.assertNull( params.getParentId() );
        Assertions.assertFalse( params.isRecursive() );
    }

    @Test
    void listByIdRecursive()
    {
        Mockito.when( contentService.list( Mockito.isA( ListContentsByParentParams.class ) ) )
            .thenReturn( ListContentsByParentResult.create().build() );

        runFunction( "/test/ListContentHandlerTest.js", "listByIdRecursive" );

        final ListContentsByParentParams params = capturedParams();
        Assertions.assertEquals( ContentId.from( "content-id" ), params.getParentId() );
        Assertions.assertNull( params.getParentPath() );
        Assertions.assertTrue( params.isRecursive() );
    }

    private ListContentsByParentParams capturedParams()
    {
        final ArgumentCaptor<ListContentsByParentParams> captor = ArgumentCaptor.forClass( ListContentsByParentParams.class );
        Mockito.verify( contentService ).list( captor.capture() );
        return captor.getValue();
    }
}
