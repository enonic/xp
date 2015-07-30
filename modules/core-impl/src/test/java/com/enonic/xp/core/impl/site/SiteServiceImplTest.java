package com.enonic.xp.core.impl.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.site.SiteDescriptor;

import static org.junit.Assert.*;

public class SiteServiceImplTest
{
    private static final String APPLICATION_NAME = "myapplication";

    private static final String UNKNOWN_APPLICATION_NAME = "unknownapplication";

    private ApplicationKey applicationKey;

    private SiteDescriptor mockedSiteDescriptor;

    private SiteServiceImpl siteService;

    @Before
    public void before()
    {
        applicationKey = ApplicationKey.from( APPLICATION_NAME );

        //Creates a mocked SiteDescriptor
        mockedSiteDescriptor = SiteDescriptor.create().build();

        //Creates a mocked SiteDescriptorRegistry
        SiteDescriptorRegistry siteDescriptorRegistry = Mockito.mock( SiteDescriptorRegistry.class );
        Mockito.when( siteDescriptorRegistry.get( applicationKey ) ).thenReturn( mockedSiteDescriptor );

        //Creates the service to test
        siteService = new SiteServiceImpl();
        siteService.setSiteDescriptorRegistry( siteDescriptorRegistry );
    }

    @Test
    public void get_descriptor()
    {
        final SiteDescriptor siteDescriptor = siteService.getDescriptor( applicationKey );
        assertEquals( mockedSiteDescriptor, siteDescriptor );
    }

    @Test
    public void get_descriptor_for_unknown_application()
    {
        final SiteDescriptor siteDescriptor = siteService.getDescriptor( ApplicationKey.from( UNKNOWN_APPLICATION_NAME ) );
        assertEquals( null, siteDescriptor );
    }
}
