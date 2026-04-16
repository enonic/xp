package com.enonic.xp.core.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UuidHelperTest
{
    @Test
    void version()
    {
        final UUID uuid = UuidHelper.newUUIDv7();
        assertEquals( 7, uuid.version() );
    }

    @Test
    void variant()
    {
        final UUID uuid = UuidHelper.newUUIDv7();
        assertEquals( 2, uuid.variant() );
    }

    @Test
    void unique()
    {
        final Set<UUID> uuids = new HashSet<>();
        for ( int i = 0; i < 1000; i++ )
        {
            uuids.add( UuidHelper.newUUIDv7() );
        }
        assertEquals( 1000, uuids.size() );
    }

    @Test
    void timestampOrdering()
        throws Exception
    {
        final UUID before = UuidHelper.newUUIDv7();
        Thread.sleep( 2 );
        final UUID after = UuidHelper.newUUIDv7();

        final long tsBefore = before.getMostSignificantBits() >>> 16;
        final long tsAfter = after.getMostSignificantBits() >>> 16;
        assertTrue( tsAfter > tsBefore, "Later UUID should have a greater timestamp" );
    }

    @Test
    void parseable()
    {
        final UUID uuid = UuidHelper.newUUIDv7();
        final UUID parsed = UUID.fromString( uuid.toString() );
        assertEquals( uuid, parsed );
    }
}
