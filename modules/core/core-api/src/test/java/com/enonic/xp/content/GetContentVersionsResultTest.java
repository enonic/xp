package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetContentVersionsResultTest
{
    @Test
    void sameVersion()
    {
        final ContentVersion version = ContentVersion.create()
            .versionId( ContentVersionId.from( "v1" ) )
            .contentId( ContentId.from( "a" ) )
            .path( ContentPath.from( "/a" ) )
            .timestamp( Instant.EPOCH )
            .comment( "comment" )
            .build();
        final ContentVersions contentVersions = ContentVersions.from( version );

        final GetContentVersionsResult result = GetContentVersionsResult.create().contentVersions( contentVersions ).totalHits( 2 ).build();

        assertEquals( contentVersions, result.getContentVersions() );
        assertEquals( 1, result.getContentVersions().getSize() );
        assertEquals( 2, result.getTotalHits() );
    }
}
