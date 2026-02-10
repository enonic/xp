package com.enonic.xp.lib.content;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersion;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.security.PrincipalKey;

import static org.mockito.Mockito.when;

class GetVersionsHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final ContentVersion version1 = ContentVersion.create()
            .versionId( ContentVersionId.from( "version1" ) )
            .contentId( ContentId.from( "contentId" ) )
            .path( ContentPath.from( "/my-content" ) )
            .timestamp( Instant.parse( "2024-01-01T00:00:00Z" ) )
            .addAction( new ContentVersion.Action( "publish", List.of(), PrincipalKey.from( "user:system:admin" ),
                                                   Instant.parse( "2024-01-01T00:00:00Z" ) ) )
            .build();

        final ContentVersion version2 = ContentVersion.create()
            .versionId( ContentVersionId.from( "version2" ) )
            .contentId( ContentId.from( "contentId" ) )
            .path( ContentPath.from( "/my-content" ) )
            .timestamp( Instant.parse( "2023-12-01T00:00:00Z" ) )
            .build();

        final GetContentVersionsResult result = GetContentVersionsResult.create()
            .totalHits( 5 )
            .cursor( "eyJ0cyI6MTcwNDA2NzIwMDAwMCwiaWQiOiJ2ZXJzaW9uMiJ9" )
            .contentVersions( ContentVersions.create().add( version1 ).add( version2 ).build() )
            .build();

        when( this.contentService.getVersions( Mockito.any( GetContentVersionsParams.class ) ) ).thenReturn( result );
        runScript( "/lib/xp/examples/content/getVersions.js" );
    }
}
