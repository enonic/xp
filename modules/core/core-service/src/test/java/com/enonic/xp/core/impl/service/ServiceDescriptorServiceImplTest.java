package com.enonic.xp.core.impl.service;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceDescriptorServiceImplTest
    extends AbstractDescriptorServiceTest
{
    private ServiceDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new ServiceDescriptorServiceImpl( this.resourceService );
    }

    @Test
    void testGetByKey()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:myservice1" );
        final ServiceDescriptor descriptor = this.service.getByKey( key );
        assertNotNull( descriptor );
        assertEquals( "[role:system.admin]", descriptor.getAllowedPrincipals().toString() );
    }

    @Test
    void testGetByKey_default()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:unknown" );
        final ServiceDescriptor descriptor = this.service.getByKey( key );
        assertNotNull( descriptor );
    }

    @Test
    void testGetByApplication()
    {
        final ServiceDescriptors descriptors = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( descriptors );
        assertEquals( 1, descriptors.getSize() );
    }
}
