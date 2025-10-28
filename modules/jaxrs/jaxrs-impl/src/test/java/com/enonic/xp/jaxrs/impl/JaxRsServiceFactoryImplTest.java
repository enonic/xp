package com.enonic.xp.jaxrs.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class JaxRsServiceFactoryImplTest
{
    private JaxRsServiceFactoryImpl factory;

    @BeforeEach
    void setup()
    {
        this.factory = new JaxRsServiceFactoryImpl();
        this.factory.setMultipartService( Mockito.mock( MultipartService.class ) );
        this.factory.activate( Mockito.mock( BundleContext.class ) );
    }

    @Test
    void newService()
    {
        final JaxRsService service = this.factory.newService( "test", "/*", null );
        assertNotNull( service );
    }
}
