package com.enonic.xp.core.impl.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.mixin.MixinService;
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

        //Creates a mocked MixinService
        MixinService mixinService = Mockito.mock( MixinService.class );
        Mockito.when( mixinService.inlineFormItems( Mockito.any() ) ).thenAnswer( invocation -> invocation.getArguments()[0] );

        //Creates the service to test
        siteService = new SiteServiceImpl();
        siteService.setSiteDescriptorRegistry( siteDescriptorRegistry );
        siteService.setMixinService( mixinService );
    }

    @Test
    public void get_descriptor()
    {
        final SiteDescriptor siteDescriptor = siteService.getDescriptor( applicationKey );
        assertEquals( mockedSiteDescriptor.getForm(), siteDescriptor.getForm() );
        assertEquals( mockedSiteDescriptor.getMetaSteps(), siteDescriptor.getMetaSteps() );
    }

    @Test
    public void get_descriptor_for_unknown_application()
    {
        final SiteDescriptor siteDescriptor = siteService.getDescriptor( ApplicationKey.from( UNKNOWN_APPLICATION_NAME ) );
        assertEquals( null, siteDescriptor );
    }
}
