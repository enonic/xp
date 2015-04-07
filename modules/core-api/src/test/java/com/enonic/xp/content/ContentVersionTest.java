package com.enonic.xp.content;

import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ContentVersionTest
{
    @Test
    public void testEquals() {

        final Instant now1 = Instant.now();

        final ContentVersion version1 = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now1 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            build();

        assertEquals( version1, version1 );
        assertEquals( version1.hashCode(), version1.hashCode() );

        assertNotEquals( version1, null );

        final Instant now2 = now1.plusMillis( 1000 );

        final ContentVersion version2 = ContentVersion.create().
            id( ContentVersionId.from( "b" ) ).
            modified( now2 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            build();

        assertNotEquals( version1, version2 );
        assertNotEquals( version1.hashCode(), version2.hashCode() );

        assertEquals( version1.getModifier(), version2.getModifier() );
        assertEquals( version1.getComment(), version2.getComment() );
        assertEquals( version1.getDisplayName(), version2.getDisplayName() );

        assertNotEquals( version1.getId(), version2.getId() );
        assertNotEquals( version1.getModified(), version2.getModified() );

    }

    @Test
    public void testCompareTo() {

        final Instant now1 = Instant.now();

        final ContentVersion version1 = ContentVersion.create().
            id( ContentVersionId.from( "a" ) ).
            modified( now1 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            build();

        assertEquals( 0, version1.compareTo( version1 ) );

        final Instant now2 = Instant.now();

        final ContentVersion version2 = ContentVersion.create().
            id( ContentVersionId.from( "b" ) ).
            modified( now2 ).
            modifier( PrincipalKey.ofAnonymous() ).
            displayName( "contentVersion" ).
            comment( "comment" ).
            build();

        assertEquals( 1, version1.compareTo( version2 ) );
        assertEquals( -1, version2.compareTo( version1 ) );

    }
}