package com.enonic.xp.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.icon.Icon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ApplicationDescriptorTest
{
    @Test
    void getters()
    {
        final Icon icon = Icon.from( new byte[]{0, 1}, "image/png", Instant.now() );
        final ApplicationKey key = ApplicationKey.from( "app" );

        final ApplicationDescriptor desc = ApplicationDescriptor.create().
            key( key ).
            description( "My Application" ).
            icon( icon ).
            build();

        assertNotNull( desc );
        assertSame( key, desc.getKey() );
        assertEquals( "My Application", desc.getDescription() );
        assertSame( icon, desc.getIcon() );
    }

    @Test
    void null_description()
    {
        final ApplicationKey key = ApplicationKey.from( "app" );
        final ApplicationDescriptor desc = ApplicationDescriptor.create().
            key( key ).
            description( null ).
            build();

        assertNotNull( desc );
        assertSame( key, desc.getKey() );
        assertEquals( "", desc.getDescription() );
    }

    @Test
    void testHashCode()
    {
        final Icon icon = Icon.from( new byte[]{0, 1}, "image/png", Instant.now() );
        final ApplicationKey key = ApplicationKey.from( "app" );

        final ApplicationDescriptor desc1 = ApplicationDescriptor.create().
            key( key ).
            description( "My Application" ).
            icon( icon ).
            build();

        final ApplicationDescriptor desc2 = ApplicationDescriptor.create().
            key( key ).
            description( "My Application" ).
            icon( icon ).
            build();

        final ApplicationDescriptor desc3 = ApplicationDescriptor.create().
            key( key ).
            build();

        assertEquals( desc1.hashCode(), desc1.hashCode() );
        assertEquals( desc1.hashCode(), desc2.hashCode() );
        assertNotEquals( desc1.hashCode(), desc3.hashCode() );
    }
}
