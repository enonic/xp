package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentVersionsTest
{
    @Test
    void testBuilder()
    {
        final Instant now1 = Instant.now();

        final ContentVersion version1 = ContentVersion.create()
            .versionId( ContentVersionId.from( "v1" ) )
            .contentId( ContentId.from( "a" ) )
            .path( ContentPath.from( "/a" ) )
            .timestamp( now1 )
            .comment( "comment" )
            .build();

        final Instant now2 = now1.plusMillis( 1000 );

        final ContentVersion version2 = ContentVersion.create()
            .versionId( ContentVersionId.from( "v2" ) )
            .contentId( ContentId.from( "a" ) )
            .path( ContentPath.from( "/a" ) )
            .timestamp( now2 )
            .comment( "comment" )
            .build();

        final ContentVersions versions = ContentVersions.create().add( version1 ).add( version2 ).build();

        assertThat( versions ).extracting( ContentVersion::versionId ).map( ContentVersionId::toString ).containsExactly( "v1", "v2" );
    }

    @Test
    void testFrom()
    {
        final Instant now1 = Instant.now();

        final ContentVersion version1 = ContentVersion.create()
            .versionId( ContentVersionId.from( "v1" ) )
            .contentId( ContentId.from( "a" ) )
            .path( ContentPath.from( "/a" ) )
            .timestamp( now1 )
            .comment( "comment" )
            .build();

        final Instant now2 = now1.plusMillis( 1000 );

        final ContentVersion version2 = ContentVersion.create()
            .versionId( ContentVersionId.from( "v2" ) )
            .contentId( ContentId.from( "a" ) )
            .path( ContentPath.from( "/a" ) )
            .timestamp( now2 )
            .comment( "comment" )
            .build();

        final ContentVersions versions = ContentVersions.from( version1, version2 );

        assertThat( versions ).extracting( ContentVersion::versionId ).map( ContentVersionId::toString ).containsExactly( "v1", "v2" );
    }
}
