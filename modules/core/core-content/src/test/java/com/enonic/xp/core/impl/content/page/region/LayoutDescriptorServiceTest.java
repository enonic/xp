package com.enonic.xp.core.impl.content.page.region;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LayoutDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected LayoutDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new LayoutDescriptorServiceImpl( this.descriptorService );
    }

    @Test
    void testGetByKey()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:mylayout" );
        final LayoutDescriptor descriptor = this.service.getByKey( key );

        assertNotNull( descriptor );
        assertTrue( Instant.now().isAfter( descriptor.getModifiedTime() ) );
    }

    @Test
    void testGetByApplication()
    {
        final LayoutDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        assertNotNull( result );
        assertEquals( 1, result.getSize() );
    }

    @Test
    void testGetByApplications()
    {
        final LayoutDescriptors result = this.service.getByApplications( ApplicationKeys.from( "myapp1", "myapp2" ) );

        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }
}
