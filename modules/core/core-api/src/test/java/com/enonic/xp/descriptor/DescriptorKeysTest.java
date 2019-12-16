package com.enonic.xp.descriptor;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DescriptorKeysTest
{
    @Test
    public void testEmpty()
    {
        final DescriptorKeys keys = DescriptorKeys.empty();
        assertEquals( "[]", keys.toString() );
    }

    @Test
    public void testFrom_array()
    {
        final DescriptorKey key1 = DescriptorKey.from( "app1:abc" );
        final DescriptorKey key2 = DescriptorKey.from( "app2:abc" );

        final DescriptorKeys keys = DescriptorKeys.from( key1, key2 );
        assertEquals( "[app1:abc, app2:abc]", keys.toString() );
    }

    @Test
    public void testFrom_iterable()
    {
        final DescriptorKey key1 = DescriptorKey.from( "app1:abc" );
        final DescriptorKey key2 = DescriptorKey.from( "app2:abc" );

        final DescriptorKeys keys = DescriptorKeys.from( List.of( key1, key2 ) );
        assertEquals( "[app1:abc, app2:abc]", keys.toString() );
    }

    @Test
    public void testFilter()
    {
        final DescriptorKey key1 = DescriptorKey.from( "app1:abc" );
        final DescriptorKey key2 = DescriptorKey.from( "app2:abc" );

        final DescriptorKeys keys1 = DescriptorKeys.from( key1, key2 );
        final DescriptorKeys keys2 = keys1.filter( ApplicationKey.from( "app1" ) );

        assertEquals( "[app1:abc]", keys2.toString() );
    }

    @Test
    public void testConcat()
    {
        final DescriptorKey key1 = DescriptorKey.from( "app1:abc" );
        final DescriptorKey key2 = DescriptorKey.from( "app2:abc" );

        final DescriptorKeys keys1 = DescriptorKeys.from( key1 );
        final DescriptorKeys keys2 = keys1.concat( DescriptorKeys.from( key1, key2 ) );

        assertEquals( "[app1:abc, app2:abc]", keys2.toString() );
    }
}
