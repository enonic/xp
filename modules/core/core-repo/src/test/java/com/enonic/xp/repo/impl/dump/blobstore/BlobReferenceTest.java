package com.enonic.xp.repo.impl.dump.blobstore;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BlobReferenceTest
{
    @Test
    void basicTest()
    {
        final Segment segment = Segment.from( "a", "b" );
        final BlobKey key = BlobKey.from( "k" );
        final BlobReference blobReference = new BlobReference( segment, key );
        assertSame( segment, blobReference.getSegment() );
        assertSame( key, blobReference.getKey() );
        assertEquals( "[a, b]:k", blobReference.toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( BlobReference.class ).verify();
    }
}
