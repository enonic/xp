package com.enonic.xp.task;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.descriptor.DescriptorKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TaskDescriptorTest
{
    @Test
    public void testDescriptor()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.SYSTEM, "test" );
        final Form form = Form.create().build();

        final TaskDescriptor descriptor = TaskDescriptor.create().
            key( key ).
            description( "test" ).
            config( form ).
            build();

        assertSame( key, descriptor.getKey() );
        assertEquals( "test", descriptor.getDescription() );
        assertSame( form, descriptor.getConfig() );
    }

    @Test
    public void testDescriptorWithoutConfig()
    {
        final DescriptorKey key = DescriptorKey.from( ApplicationKey.SYSTEM, "test" );

        final TaskDescriptor descriptor = TaskDescriptor.create().
            key( key ).
            description( "test" ).
            build();

        assertSame( key, descriptor.getKey() );
        assertEquals( "test", descriptor.getDescription() );
        assertNotNull( descriptor.getConfig() );
    }
}
