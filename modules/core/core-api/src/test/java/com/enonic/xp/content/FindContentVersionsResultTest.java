package com.enonic.xp.content;

import java.time.Instant;

import org.junit.Test;

import static org.junit.Assert.*;

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
            contentId( ContentId.from( "a" ) ).
            build();

        final FindContentVersionsResult result = FindContentVersionsResult.create().
            contentVersions( contentVersions ).from( 0 ).hits( 2 ).size( 2 ).totalHits( 2 ).
            build();

        assertEquals( result.getContentVersions(), contentVersions );
        assertEquals( result.getFrom(), 0 );
        assertEquals( result.getSize(), 2 );
        assertEquals( result.getHits(), 2 );
        assertEquals( result.getTotalHits(), 2 );


    }

}