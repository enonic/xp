package com.enonic.xp.descriptor;

import org.junit.Test;

import static org.junit.Assert.*;

public class DescriptorTest
{
    private final class MyDescriptor
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
