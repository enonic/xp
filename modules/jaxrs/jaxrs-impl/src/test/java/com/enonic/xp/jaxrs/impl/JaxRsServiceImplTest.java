package com.enonic.xp.jaxrs.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Lists;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.web.dispatch.ServletMapping;

import static org.junit.Assert.*;

public class JaxRsServiceImplTest
{
    private BundleContext context;

    private JaxRsServiceImpl service;

    @Before
    public void setup()
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
    public void testInitDestroy()
    {
        final ServiceRegistration<ServletMapping> reg = mockRegistration();
        Mockito.when( this.context.registerService( Mockito.eq( ServletMapping.class ), Mockito.any( ServletMapping.class ),
                                                    Mockito.any() ) ).thenReturn( reg );

        this.service.init();
        this.service.destroy();

        Mockito.verify( reg, Mockito.times( 1 ) ).unregister();
    }

    @Test
    public void testAddRemoveService()
    {
        final ServiceReference<JaxRsComponent> ref = mockReference();
        Mockito.when( ref.getProperty( "group" ) ).thenReturn( "test" );

        final JaxRsComponent component = Mockito.mock( JaxRsComponent.class );
        Mockito.when( this.context.getService( ref ) ).thenReturn( component );

        assertSame( component, this.service.addingService( ref ) );
        assertEquals( 1, Lists.newArrayList( this.service.iterator() ).size() );

        this.service.removedService( ref, component );
        assertEquals( 0, Lists.newArrayList( this.service.iterator() ).size() );
    }

    @Test
    public void testModifiedService()
    {
        final ServiceReference<JaxRsComponent> ref = mockReference();
        final JaxRsComponent component = Mockito.mock( JaxRsComponent.class );

        this.service.modifiedService( ref, component );
    }

    @Test
    public void testAddingService_not_in_group()
    {
        final ServiceReference<JaxRsComponent> ref = mockReference();
        Mockito.when( ref.getProperty( "group" ) ).thenReturn( "other" );

        assertNull( this.service.addingService( ref ) );
    }
}
