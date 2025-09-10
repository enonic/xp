package com.enonic.xp.core.impl.style;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StyleDescriptorServiceImplTest
    extends ApplicationTestSupport

{
    private StyleDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );

        this.service = new StyleDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setApplicationService( this.applicationService );
    }

    @Test
    public void getByApplication()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp1" );
        final StyleDescriptor descriptor = this.service.getByApplication( appKey );
        assertNotNull( descriptor );
        assertEquals( descriptor.getApplicationKey(), appKey );
        assertTrue( Instant.now().isAfter( descriptor.getModifiedTime() ) );
    }

    @Test
    public void getByApplications()
    {
        final ApplicationKeys appKeys = ApplicationKeys.from( "myapp1", "myapp2" );
        final StyleDescriptors descriptors = this.service.getByApplications( appKeys );
        assertNotNull( descriptors );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    public void getAll()
    {
        final ApplicationKey appKey = ApplicationKey.from( "myapp1" );
        final StyleDescriptors descriptors = this.service.getAll();
        assertNotNull( descriptors );
        assertEquals( 2, descriptors.getSize() );
    }

    @Test
    public void getByApplicationInvalidStyles()
    {
        addApplication( "myapp3", "/apps/myapp3" );

        final ApplicationKey appKey = ApplicationKey.from( "myapp3" );
        final StyleDescriptor descriptor = this.service.getByApplication( appKey );
        assertNull( descriptor );
    }
}
