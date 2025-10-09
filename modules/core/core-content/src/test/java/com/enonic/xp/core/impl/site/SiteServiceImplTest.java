package com.enonic.xp.core.impl.site;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.site.SiteDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SiteServiceImplTest
    extends ApplicationTestSupport
{
    protected SiteServiceImpl service;

    @Override
    protected void initialize()
    {
        addApplication( "myapp", "/apps/myapp" );

        this.service = new SiteServiceImpl( resourceService );
    }

    @Test
    public void get_descriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );
        assertEquals( 2, siteDescriptor.getResponseProcessors().getSize() );
        assertEquals( "filter1", siteDescriptor.getResponseProcessors().get( 0 ).getName() );
        assertEquals( 20, siteDescriptor.getResponseProcessors().get( 1 ).getOrder() );
        assertTrue( Instant.now().isAfter( siteDescriptor.getModifiedTime() ) );
    }

    @Test
    public void get_portal_descriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.PORTAL;
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );

        assertNull( siteDescriptor );
    }

    @Test
    public void get_descriptor_for_unknown_application()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "unknown" );
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );
        assertNull( siteDescriptor );
    }
}
