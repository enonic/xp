package com.enonic.xp.descriptor;

import org.junit.jupiter.api.Test;

import com.enonic.xp.page.DescriptorKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DescriptorTest
{
    private static final class MyDescriptor
        extends Descriptor
    {
        MyDescriptor( final String key )
        {
            super( DescriptorKey.from( key ) );
        }
    }

    @Test
    public void testAccessors()
    {
        final MyDescriptor descriptor = new MyDescriptor( "app:abc" );
        assertEquals( "app:abc", descriptor.getKey().toString() );
        assertEquals( "abc", descriptor.getName() );
        assertEquals( "app", descriptor.getApplicationKey().toString() );
    }
}
