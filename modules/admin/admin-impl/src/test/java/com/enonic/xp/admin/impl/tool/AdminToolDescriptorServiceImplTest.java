package com.enonic.xp.admin.impl.tool;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdminToolDescriptorServiceImplTest
    extends ApplicationTestSupport
{
    protected AdminToolDescriptorServiceImpl service;

    @Override
    protected void initialize()
    {
        this.service = new AdminToolDescriptorServiceImpl( this.resourceService, this.applicationService );

        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
    }

    @Test
    void getAllowedAdminToolDescriptors()
    {
        final PrincipalKeys principalKeys = PrincipalKeys.from( PrincipalKey.from( "role:system.user.admin" ) );
        AdminToolDescriptors result = this.service.getAllowedAdminToolDescriptors( principalKeys );
        assertNotNull( result );
        assertEquals( 1, result.getSize() );

        result = this.service.getAllowedAdminToolDescriptors( PrincipalKeys.empty() );
        assertNotNull( result );
        assertEquals( 0, result.getSize() );
    }

    @Test
    void getByApplication()
    {
        final AdminToolDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        assertEquals( 1, result.getSize() );

        final AdminToolDescriptor adminToolDescriptor = result.get( 0 );

        assertEquals( "My admin tool", adminToolDescriptor.getDisplayName() );
        assertEquals( "My admin tool description", adminToolDescriptor.getDescription() );
        assertEquals( 1, adminToolDescriptor.getAllowedPrincipals().getSize() );
    }


    @Test
    void getByKey()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myadmintool" );
        final AdminToolDescriptor result = this.service.getByKey( descriptorKey );

        assertNotNull( result );
        assertEquals( "My admin tool", result.getDisplayName() );
        assertEquals( "My admin tool description", result.getDescription() );
        assertEquals( 1, result.getAllowedPrincipals().getSize() );
    }

    @Test
    void getIconByKey()
    {
        final DescriptorKey descriptorKey = DescriptorKey.from( ApplicationKey.from( "myapp1" ), "myadmintool" );
        final String icon = this.service.getIconByKey( descriptorKey );

        assertNull( icon );

        final DescriptorKey descriptorKey2 = DescriptorKey.from( ApplicationKey.from( "myapp2" ), "myadmintool" );
        final String icon2 = this.service.getIconByKey( descriptorKey2 );

        assertEquals( "<svg>SVG content</svg>", icon2 );
    }
}
