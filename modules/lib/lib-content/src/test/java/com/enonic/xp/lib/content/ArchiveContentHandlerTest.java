package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;

import static org.mockito.Mockito.when;

class ArchiveContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( ContentPath.from( "/path/to/mycontent" ) ) ).thenReturn( content );

        when( this.contentService.archive( Mockito.isA( ArchiveContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeArchive( (ArchiveContentParams) invocationOnMock.getArguments()[0] ) );

        runScript( "/lib/xp/examples/content/archive.js" );
    }

    private ArchiveContentsResult invokeArchive( final ArchiveContentParams params )
    {
        return ArchiveContentsResult.create().addArchived( params.getContentId() ).build();
    }
}
