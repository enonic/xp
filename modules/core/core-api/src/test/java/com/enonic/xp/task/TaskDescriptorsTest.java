package com.enonic.xp.task;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.Assert.*;

public class TaskDescriptorsTest
{
    private TaskDescriptor desc1;

    private TaskDescriptor desc2;

    @Before
    public void setup()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.SYSTEM, "test" );
        this.desc1 = TaskDescriptor.create().key( key ).build();
        this.desc2 = TaskDescriptor.create().key( key ).build();
    }

    @Test
    public void testFrom_array()
    {
        final TaskDescriptors descriptors = TaskDescriptors.from( this.desc1, this.desc2 );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    public void testFrom_iterable()
    {
        final TaskDescriptors descriptors = TaskDescriptors.from( Lists.newArrayList( this.desc1, this.desc2 ) );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    public void testEmpty()
    {
        final TaskDescriptors descriptors = TaskDescriptors.empty();
        assertEquals( 0, descriptors.getSize() );
    }
}
