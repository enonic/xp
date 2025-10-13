package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentVersionTest
{
    @Test
    void testBuilder()
    {
        final Instant now1 = Instant.now();
        final Instant now2 = Instant.now();

        final ContentVersion version = ContentVersion.create()
            .id( ContentVersionId.from( "a" ) )
            .path( ContentPath.from( ContentPath.ROOT, "a" ) )
            .modified( now1 )
            .timestamp( now2 )
            .changedBy( PrincipalKey.ofAnonymous() )
            .comment( "comment" )
            .build();

        assertEquals( ContentVersionId.from( "a" ), version.getId() );
        assertEquals( now1, version.getChangedTime() );
        assertEquals( now2, version.getTimestamp() );
        assertEquals( "comment", version.getComment() );
        assertEquals( PrincipalKey.ofAnonymous(), version.getChangedBy() );
        assertEquals( "/a", version.getPath().toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ContentVersion.class ).verify();
    }
}
