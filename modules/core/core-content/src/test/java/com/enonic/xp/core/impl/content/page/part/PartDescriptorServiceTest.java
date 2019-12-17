package com.enonic.xp.core.impl.content.page.part;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.core.impl.content.page.region.PartDescriptorServiceImpl;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertNotNull( descriptor );
    }

    @Test
    public void testGetByApplication()
        throws Exception
    {
        final PartDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        assertNotNull( result );
        assertEquals( 1, result.getSize() );
    }

    @Test
    public void testGetByApplications()
        throws Exception
    {
        final PartDescriptors result = this.service.getByApplications( ApplicationKeys.from( "myapp1", "myapp2" ) );

        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }
}
