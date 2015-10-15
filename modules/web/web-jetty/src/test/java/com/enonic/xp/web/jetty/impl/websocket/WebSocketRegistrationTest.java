package com.enonic.xp.web.jetty.impl.websocket;

import java.util.Dictionary;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.springframework.mock.web.MockServletContext;

import com.google.common.collect.Lists;

import com.enonic.xp.web.websocket.WebSocketHandler;

import static org.junit.Assert.*;

public class WebSocketRegistrationTest
{
    private WebSocketRegistration reg;

    private BundleContext bundleContext;

    private ServletContext servletContext;

    private List<ServiceRegistration> registrations;

    @Before
    public void setup()
        throws Exception
    {
        this.servletContext = new MockServletContext();

        this.bundleContext = Mockito.mock( BundleContext.class, (Answer) this::defaultAnswer );
        this.registrations = Lists.newArrayList();

        final WebSocketHandler handler = Mockito.mock( WebSocketHandler.class );
        Mockito.when( handler.getPath() ).thenReturn( "/ws" );

        this.reg = new WebSocketRegistration( handler );
    }

    @Test
    public void testSingleRegistration()
    {
        assertEquals( 0, this.registrations.size() );
        this.reg.register( this.bundleContext, this.servletContext );

        assertEquals( 1, this.registrations.size() );

        final ServiceReference ref = this.registrations.get( 0 ).getReference();
        assertNotNull( ref );

        final Object service = this.bundleContext.getService( ref );
        assertNotNull( service );
        assertTrue( service instanceof WebSocketServlet );

        assertEquals( "/ws", ref.getProperty( "osgi.http.whiteboard.servlet.pattern" ) );

        this.reg.unregister();
        assertEquals( 0, this.registrations.size() );
    }

    @Test
    public void testDoubleRegistration()
    {
        assertEquals( 0, this.registrations.size() );
        this.reg.register( this.bundleContext, this.servletContext );
        this.reg.register( this.bundleContext, this.servletContext );

        assertEquals( 1, this.registrations.size() );

        final ServiceReference ref = this.registrations.get( 0 ).getReference();
        assertNotNull( ref );

        final Object service = this.bundleContext.getService( ref );
        assertNotNull( service );
        assertTrue( service instanceof WebSocketServlet );

        assertEquals( "/ws", ref.getProperty( "osgi.http.whiteboard.servlet.pattern" ) );

        this.reg.unregister();
        this.reg.unregister();

        assertEquals( 0, this.registrations.size() );
    }

    private Object defaultAnswer( final InvocationOnMock invocation )
    {
        if ( invocation.getMethod().getName().equals( "registerService" ) )
        {
            return mockRegisterService( invocation );
        }

        return null;
    }

    private ServiceRegistration mockRegisterService( final InvocationOnMock invocation )
    {
        final Servlet servlet = (Servlet) invocation.getArguments()[1];
        final Dictionary config = (Dictionary) invocation.getArguments()[2];

        final ServiceReference ref = mockServiceReference( config );

        final ServiceRegistration reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( reg.getReference() ).thenReturn( ref );

        Mockito.when( this.bundleContext.getService( ref ) ).thenReturn( servlet );

        Mockito.doAnswer( i -> {
            registrations.remove( reg );
            return null;
        } ).when( reg ).unregister();

        this.registrations.add( reg );
        return reg;
    }

    private ServiceReference mockServiceReference( final Dictionary config )
    {
        final ServiceReference ref = Mockito.mock( ServiceReference.class );
        Mockito.when( ref.getProperty( Mockito.any() ) ).then( invocation -> config.get( invocation.getArguments()[0] ) );
        return ref;
    }
}
