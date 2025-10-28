package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentVersionDateComparatorTest
{
    @Test
    void testComparison()
    {
        final Instant now1 = Instant.now();

        final ContentVersion version1 = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now1 ).
            timestamp( now1 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            build();

        final ContentVersion version1Same = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now1 ).
            timestamp( now1 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            build();

        assertEquals( 0, ContentVersionDateComparator.INSTANCE.compare( version1, version1Same ) );

        final Instant now2 = now1.plusMillis( 1000 );

        final ContentVersion version2 = ContentVersion.create().
            id( ContentVersionId.from( "b" ) ).
            modified( now2 ).
            timestamp( now2 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            build();

        assertEquals( 1, ContentVersionDateComparator.INSTANCE.compare( version1, version2 ) );
        assertEquals( -1, ContentVersionDateComparator.INSTANCE.compare( version2, version1 ) );

    }
}
