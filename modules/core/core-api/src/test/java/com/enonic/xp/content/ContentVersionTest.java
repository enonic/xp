package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentVersionTest
{
    @Test
    void testBuilder()
    {
        final Instant now2 = Instant.now();

        final ContentVersion version = ContentVersion.create()
            .versionId( ContentVersionId.from( "a" ) )
            .contentId( ContentId.from( "1" ) )
            .path( ContentPath.from( ContentPath.ROOT, "a" ) )
            .timestamp( now2 )
            .comment( "comment" )
            .build();

        assertEquals( ContentVersionId.from( "a" ), version.versionId() );
        assertEquals( ContentId.from( "1" ), version.contentId() );
        assertEquals( now2, version.timestamp() );
        assertEquals( "comment", version.comment() );
        assertEquals( "/a", version.path().toString() );
    }
}
