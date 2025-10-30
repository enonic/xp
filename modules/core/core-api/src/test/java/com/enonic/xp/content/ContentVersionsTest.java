package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentVersionsTest
{
    @Test
    void testBuilder()
    {

        final Instant now1 = Instant.now();

        final ContentVersion version1 = ContentVersion.create().versionId( ContentVersionId.from( "a" ) ).
            timestamp( now1 ).
            comment( "comment" ).
            build();

        final Instant now2 = now1.plusMillis( 1000 );

        final ContentVersion version2 = ContentVersion.create().versionId( ContentVersionId.from( "b" ) ).
            timestamp( now2 ).
            comment( "comment" ).
            build();

        final ContentVersions versions = ContentVersions.create().
            add( version1 ).
            add( version2 ).
            build();

        assertTrue( versions.iterator().hasNext() );
    }
}
