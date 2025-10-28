package com.enonic.xp.jaxrs.impl;

import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.web.dispatch.ServletMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class JaxRsServiceImplTest
{
    private BundleContext context;

    private JaxRsServiceImpl service;

    @BeforeEach
    void setup()
    {
        this.context = Mockito.mock( BundleContext.class );
        this.service = new JaxRsServiceImpl( this.context, "test", "/*", null );
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceReference<T> mockReference()
    {
        return Mockito.mock( ServiceReference.class );
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceRegistration<T> mockRegistration()
    {
        return Mockito.mock( ServiceRegistration.class );
    }

    @Test
    void testInitDestroy()
    {
        final ServiceRegistration<ServletMapping> reg = mockRegistration();
        Mockito.when( this.context.registerService( Mockito.eq( ServletMapping.class ), Mockito.any( ServletMapping.class ),
                                                    Mockito.any() ) ).thenReturn( reg );

        this.service.init();
        this.service.destroy();

        Mockito.verify( reg, Mockito.times( 1 ) ).unregister();
    }

    @Test
    void testAddRemoveService()
    {
        final ServiceReference<JaxRsComponent> ref = mockReference();
        Mockito.when( ref.getProperty( "group" ) ).thenReturn( "test" );

        final JaxRsComponent component = Mockito.mock( JaxRsComponent.class );
        Mockito.when( this.context.getService( ref ) ).thenReturn( component );

        assertSame( component, this.service.addingService( ref ) );
        assertEquals( 1, StreamSupport.stream( this.service.spliterator(), false ).count() );

        this.service.removedService( ref, component );
        assertEquals( 0, StreamSupport.stream( this.service.spliterator(), false ).count() );
    }

    @Test
    void testModifiedService()
    {
        final ServiceReference<JaxRsComponent> ref = mockReference();
        final JaxRsComponent component = Mockito.mock( JaxRsComponent.class );

        this.service.modifiedService( ref, component );
    }

    @Test
    void testAddingService_not_in_group()
    {
        final ServiceReference<JaxRsComponent> ref = mockReference();
        Mockito.when( ref.getProperty( "group" ) ).thenReturn( "other" );

        assertNull( this.service.addingService( ref ) );
    }
}
