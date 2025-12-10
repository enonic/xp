package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindContentVersionsResultTest
{
    @Test
    void sameVersion()
    {
        final ContentVersion version = ContentVersion.create().versionId( ContentVersionId.from( "a" ) ).build();
        final ContentVersions contentVersions = ContentVersions.from( version );

        final FindContentVersionsResult result =
            FindContentVersionsResult.create().contentVersions( contentVersions ).totalHits( 2 ).build();

        assertEquals( contentVersions, result.getContentVersions() );
        assertEquals( 1, result.getContentVersions().getSize() );
        assertEquals( 2, result.getTotalHits() );
    }
}
