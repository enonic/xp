package com.enonic.xp.core.impl.content.page;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageDescriptorServiceTest
    extends AbstractDescriptorServiceTest
{
    protected PageDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new PageDescriptorServiceImpl( this.descriptorService );
    }

    @Test
    void testGetByKey()
    {
        final DescriptorKey key = DescriptorKey.from( "myapp1:mypage" );
        final PageDescriptor descriptor = this.service.getByKey( key );

        assertNotNull( descriptor );
        assertTrue( Instant.now().isAfter( descriptor.getModifiedTime() ) );
    }

    @Test
    void testGetByApplication()
    {
        final PageDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        assertNotNull( result );
        assertEquals( 1, result.getSize() );
    }

    @Test
    void testGetByApplications()
    {
        final PageDescriptors result = this.service.getByApplications( ApplicationKeys.from( "myapp1", "myapp2" ) );

        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }
}
