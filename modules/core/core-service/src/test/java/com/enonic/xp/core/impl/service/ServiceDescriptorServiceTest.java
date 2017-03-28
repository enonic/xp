package com.enonic.xp.core.impl.service;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.service.ServiceDescriptor;

public class ServiceDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected ServiceDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new ServiceDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void testGetByKey()
        throws Exception
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:myservice" );
        final ServiceDescriptor descriptor = this.service.getByKey( key );
        Assert.assertNotNull( descriptor );
    }
}
