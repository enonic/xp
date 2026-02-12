package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.security.PrincipalKey;

import static org.mockito.Mockito.when;

class GetActiveVersionsHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final ContentVersion draftVersion = ContentVersion.create()
            .versionId( ContentVersionId.from( "draftVersion" ) )
            .contentId( ContentId.from( "contentId" ) )
            .path( ContentPath.from( "/my-content" ) )
            .timestamp( Instant.parse( "2024-01-01T00:00:00Z" ) )
            .addAction( new ContentVersion.Action( "publish", List.of(), PrincipalKey.from( "user:system:admin" ),
                                                   Instant.parse( "2024-01-01T00:00:00Z" ) ) )
            .build();

        final ContentVersion masterVersion = ContentVersion.create()
            .versionId( ContentVersionId.from( "masterVersion" ) )
            .contentId( ContentId.from( "contentId" ) )
            .path( ContentPath.from( "/my-content" ) )
            .timestamp( Instant.parse( "2023-12-01T00:00:00Z" ) )
            .build();

        final GetActiveContentVersionsResult result = GetActiveContentVersionsResult.create()
            .add( Branch.from( "draft" ), draftVersion )
            .add( Branch.from( "master" ), masterVersion )
            .build();

        when( this.contentService.getActiveVersions( Mockito.any( GetActiveContentVersionsParams.class ) ) ).thenReturn( result );
        runScript( "/lib/xp/examples/content/getActiveVersions.js" );
    }
}
