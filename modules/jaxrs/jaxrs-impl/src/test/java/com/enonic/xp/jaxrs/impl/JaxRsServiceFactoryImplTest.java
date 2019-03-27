package com.enonic.xp.jaxrs.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.web.multipart.MultipartService;

import static org.junit.Assert.*;

public class JaxRsServiceFactoryImplTest
{
    private JaxRsServiceFactoryImpl factory;

    @Before
    public void setup()
    {
        this.factory = new JaxRsServiceFactoryImpl();
        this.factory.setMultipartService( Mockito.mock( MultipartService.class ) );
        this.factory.activate( Mockito.mock( BundleContext.class ) );
    }

    @Test
    public void newService()
    {
        final JaxRsService service = this.factory.newService( "test", "/*", null );
        assertNotNull( service );
    }
}
