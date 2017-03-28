package com.enonic.xp.descriptor;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class DescriptorsTest
{
    private final class MyDescriptor
        extends Descriptor
    {
        MyDescriptor( final String key )
        {
            super( DescriptorKey.from( key ) );
        }

        @Override
        public String toString()
        {
            return this.getKey().toString();
        }
    }

    @Test
    public void testEmpty()
    {
        final Descriptors list = Descriptors.empty();
        assertEquals( "[]", list.toString() );
    }

    @Test
    public void testFrom_array()
    {
        final MyDescriptor desc1 = new MyDescriptor( "app1:abc" );
        final MyDescriptor desc2 = new MyDescriptor( "app2:abc" );

        final Descriptors<MyDescriptor> list = Descriptors.from( desc1, desc2 );
        assertEquals( "[app1:abc, app2:abc]", list.toString() );
    }

    @Test
    public void testFrom_iterable()
    {
        final MyDescriptor desc1 = new MyDescriptor( "app1:abc" );
        final MyDescriptor desc2 = new MyDescriptor( "app2:abc" );

        final Descriptors<MyDescriptor> list = Descriptors.from( Lists.newArrayList( desc1, desc2 ) );
        assertEquals( "[app1:abc, app2:abc]", list.toString() );
    }

    @Test
    public void testFilter()
    {
        final MyDescriptor desc1 = new MyDescriptor( "app1:abc" );
        final MyDescriptor desc2 = new MyDescriptor( "app2:abc" );

        final Descriptors<MyDescriptor> list1 = Descriptors.from( desc1, desc2 );
        final Descriptors<MyDescriptor> list2 = list1.filter( ApplicationKey.from( "app1" ) );

        assertEquals( "[app1:abc]", list2.toString() );
    }
}
