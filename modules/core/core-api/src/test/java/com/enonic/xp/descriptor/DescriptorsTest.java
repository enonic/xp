package com.enonic.xp.descriptor;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        final Descriptors<MyDescriptor> list = Descriptors.from( List.of( desc1, desc2 ) );
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
