package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindContentVersionsResultTest
{
    @Test
    public void sameVersion()
        throws Exception
    {
        final Instant now = Instant.now();

        final ContentVersion version = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now ).
            build();

        final ContentVersions contentVersions = ContentVersions.create().
            add( version ).
            build();

        final FindContentVersionsResult result = FindContentVersionsResult.create().
            contentVersions( contentVersions ).totalHits( 2 ).
            build();

        assertEquals( contentVersions, result.getContentVersions() );
        assertEquals( 1, result.getContentVersions().getSize() );
        assertEquals( 2, result.getTotalHits() );
    }
}
