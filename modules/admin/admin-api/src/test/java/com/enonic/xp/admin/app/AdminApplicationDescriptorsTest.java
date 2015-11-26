package com.enonic.xp.admin.app;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.xp.admin.app.AdminApplicationDescriptor;
import com.enonic.xp.admin.app.AdminApplicationDescriptors;
import com.enonic.xp.page.DescriptorKey;

import static org.junit.Assert.*;

public class AdminApplicationDescriptorsTest
{
    @Test
    public void empty()
    {
        assertTrue( AdminApplicationDescriptors.empty().isEmpty() );
    }

    @Test
    public void from()
    {
        final AdminApplicationDescriptor adminApplicationDescriptor1 = AdminApplicationDescriptor.create().
            name( "My adminApplication" ).
            key( DescriptorKey.from( "module:my-adminApplication" ) ).
            build();

        final AdminApplicationDescriptor adminApplicationDescriptor2 = AdminApplicationDescriptor.create().
            name( "My second adminApplication" ).
            key( DescriptorKey.from( "module:my-second-adminApplication" ) ).
            build();

        assertEquals( 2, AdminApplicationDescriptors.from( adminApplicationDescriptor1, adminApplicationDescriptor2 ).getSize() );
        assertEquals( 2, AdminApplicationDescriptors.from(
            AdminApplicationDescriptors.from( adminApplicationDescriptor1, adminApplicationDescriptor2 ) ).getSize() );
        assertEquals( 2, AdminApplicationDescriptors.from(
            Arrays.asList( adminApplicationDescriptor1, adminApplicationDescriptor2 ) ).getSize() );
    }

}
