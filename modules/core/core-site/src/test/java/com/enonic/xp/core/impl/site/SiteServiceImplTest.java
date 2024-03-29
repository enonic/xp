package com.enonic.xp.core.impl.site;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.SiteDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SiteServiceImplTest
    extends ApplicationTestSupport
{
    protected MixinService mixinService;

    protected SiteServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        Mockito.when( this.mixinService.inlineFormItems( Mockito.any() ) ).
            thenAnswer( ( invocation ) -> invocation.getArguments()[0] );
        addApplication( "myapp", "/apps/myapp" );

        this.service = new SiteServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setMixinService( this.mixinService );
    }

    @Test
    public void get_descriptor()
        throws Exception
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );
        assertEquals( 1, siteDescriptor.getForm().getFormItems().size() );
        assertEquals( 2, siteDescriptor.getXDataMappings().getSize() );
        assertEquals( 2, siteDescriptor.getResponseProcessors().getSize() );
        assertEquals( "filter1", siteDescriptor.getResponseProcessors().get( 0 ).getName() );
        assertEquals( 20, siteDescriptor.getResponseProcessors().get( 1 ).getOrder() );
        assertTrue( Instant.now().isAfter( siteDescriptor.getModifiedTime() ) );
    }

    @Test
    public void get_descriptor_for_unknown_application()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "unknown" );
        final SiteDescriptor siteDescriptor = this.service.getDescriptor( applicationKey );
        assertEquals( null, siteDescriptor );
    }
}
