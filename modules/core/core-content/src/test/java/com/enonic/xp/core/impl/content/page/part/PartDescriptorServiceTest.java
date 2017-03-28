package com.enonic.xp.core.impl.content.page.part;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptors;

public class PartDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PartDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new PartDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setMixinService( this.mixinService );
    }

    @Test
    public void testGetByKey()
        throws Exception
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:mypart" );
        final PartDescriptor descriptor = this.service.getByKey( key );
        Assert.assertNotNull( descriptor );
    }

    @Test
    public void testGetByApplication()
        throws Exception
    {
        final PartDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        Assert.assertNotNull( result );
        Assert.assertEquals( 1, result.getSize() );
    }

    @Test
    public void testGetByApplications()
        throws Exception
    {
        final PartDescriptors result = this.service.getByApplications( ApplicationKeys.from( "myapp1", "myapp2" ) );

        Assert.assertNotNull( result );
        Assert.assertEquals( 2, result.getSize() );
    }
}
