package com.enonic.xp.convert;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceKeyConverterTest
{
    @Test
    void testSameType()
    {
        final ResourceKey key = ResourceKey.from( "myapplication:/some/path" );
        assertSame( key, Converters.convert( key, ResourceKey.class ) );
    }

    @Test
    void testToString()
    {
        assertEquals( ResourceKey.from( "myapplication:/some/path" ), Converters.convert( "myapplication:/some/path", ResourceKey.class ) );
        assertEquals( ResourceKey.from( "myapplication:/some/path" ),
                      Converters.convertOrDefault( null, ResourceKey.class, ResourceKey.from( "myapplication:/some/path" ) ) );

    }

    @Test
    void testFailure()
    {
        assertThrows(ConvertException.class, () -> Converters.convert( "/some/path", ResourceKey.class ));
    }
}
