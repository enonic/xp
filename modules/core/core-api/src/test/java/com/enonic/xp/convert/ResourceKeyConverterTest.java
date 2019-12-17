package com.enonic.xp.convert;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResourceKeyConverterTest
{
    @Test
    public void testSameType()
    {
        final ResourceKey key = ResourceKey.from( "myapplication:/some/path" );
        assertSame( key, Converters.convert( key, ResourceKey.class ) );
    }

    @Test
    public void testToString()
    {
        assertEquals( ResourceKey.from( "myapplication:/some/path" ), Converters.convert( "myapplication:/some/path", ResourceKey.class ) );
        assertEquals( ResourceKey.from( "myapplication:/some/path" ),
                      Converters.convertOrDefault( null, ResourceKey.class, ResourceKey.from( "myapplication:/some/path" ) ) );

    }

    @Test
    public void testFailure()
    {
        assertThrows(ConvertException.class, () -> Converters.convert( "/some/path", ResourceKey.class ));
    }
}
