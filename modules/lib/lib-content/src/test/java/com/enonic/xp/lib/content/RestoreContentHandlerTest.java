package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;

import static org.mockito.Mockito.when;

class RestoreContentHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        when( this.contentService.getByPath( ContentPath.from( "/path/to/mycontent" ) ) ).thenReturn( content );

        when( this.contentService.restore( Mockito.isA( RestoreContentParams.class ) ) ).thenAnswer(
            invocationOnMock -> invokeRestore( (RestoreContentParams) invocationOnMock.getArguments()[0], content ) );

        runScript( "/lib/xp/examples/content/restore.js" );
    }

    private RestoreContentsResult invokeRestore( final RestoreContentParams params, final Content content )
    {
        return RestoreContentsResult.create().addRestored( params.getContentId() ).parentPath( content.getPath() ).build();
    }
}
