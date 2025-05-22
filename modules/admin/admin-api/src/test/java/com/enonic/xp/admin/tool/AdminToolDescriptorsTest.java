package com.enonic.xp.admin.tool;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminToolDescriptorsTest
{
    @Test
    public void empty()
    {
        assertTrue( AdminToolDescriptors.empty().isEmpty() );
    }

    @Test
    public void from()
    {
        final AdminToolDescriptor adminToolDescriptor1 = AdminToolDescriptor.create().
            displayName( "My admin tool" ).
            addAllowedPrincipals( PrincipalKey.from( "role:system.admin" ) ).
            key( DescriptorKey.from( "module:my-admin-tool" ) ).
            build();

        final AdminToolDescriptor adminToolDescriptor2 = AdminToolDescriptor.create().
            displayName( "My second admin tool 2" ).
            key( DescriptorKey.from( "module:my-second-admin-tool" ) ).
            build();

        assertEquals( 2, AdminToolDescriptors.from( adminToolDescriptor1, adminToolDescriptor2 ).getSize() );
        assertEquals( 2, AdminToolDescriptors.from( AdminToolDescriptors.from( adminToolDescriptor1, adminToolDescriptor2 ) ).getSize() );
        assertEquals( 2, AdminToolDescriptors.from( Arrays.asList( adminToolDescriptor1, adminToolDescriptor2 ) ).getSize() );
    }

}
