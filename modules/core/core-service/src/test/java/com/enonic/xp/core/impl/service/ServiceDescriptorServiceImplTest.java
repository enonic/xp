package com.enonic.xp.core.impl.service;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.service.ServiceDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServiceDescriptorServiceImplTest
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
    public void testGetByKey()
        throws Exception
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:myservice1" );
        final ServiceDescriptor descriptor = this.service.getByKey( key );
        assertNotNull( descriptor );
        assertEquals( "[role:system.admin]", descriptor.getAllowedPrincipals().toString() );
    }

    @Test
    public void testGetByKey_default()
        throws Exception
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:unknown" );
        final ServiceDescriptor descriptor = this.service.getByKey( key );
        assertNotNull( descriptor );
    }

    @Test
    public void testGetByApplication()
        throws Exception
    {
        final ServiceDescriptors descriptors = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( descriptors );
        assertEquals( 1, descriptors.getSize() );
    }
}
