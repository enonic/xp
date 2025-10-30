package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentVersionTest
{
    @Test
    void testBuilder()
    {
        final Instant now2 = Instant.now();

        final ContentVersion version = ContentVersion.create()
            .versionId( ContentVersionId.from( "a" ) )
            .path( ContentPath.from( ContentPath.ROOT, "a" ) )
            .timestamp( now2 )
            .comment( "comment" )
            .build();

        assertEquals( ContentVersionId.from( "a" ), version.getVersionId() );
        assertEquals( now2, version.getTimestamp() );
        assertEquals( "comment", version.getComment() );
        assertEquals( "/a", version.getPath().toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ContentVersion.class ).verify();
    }
}
