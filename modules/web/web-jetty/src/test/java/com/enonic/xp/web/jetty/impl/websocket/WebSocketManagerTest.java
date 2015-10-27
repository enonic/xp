package com.enonic.xp.web.jetty.impl.websocket;

import java.util.Dictionary;
import java.util.List;

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

import com.enonic.xp.web.jetty.impl.JettyController;
import com.enonic.xp.web.websocket.WebSocketHandler;

import static org.junit.Assert.*;

public class WebSocketManagerTest
{
    private WebSocketManager manager;

    private BundleContext context;

    private TestWebSocketHandler handler1;

    private TestWebSocketHandler handler2;

    private List<ServiceRegistration> registrations;

    @Before
    public void setup()
        throws Exception
    {
        this.context = Mockito.mock( BundleContext.class, (Answer) this::defaultAnswer );
        this.registrations = Lists.newArrayList();

        final WebSocketHandler handler = Mockito.mock( WebSocketHandler.class );
        Mockito.when( handler.getPath() ).thenReturn( "/ws" );

        final JettyController controller = Mockito.mock( JettyController.class );
        Mockito.when( controller.getServletContext() ).thenReturn( new MockServletContext() );

        this.manager = new WebSocketManager();
        this.manager.setController( controller );

        this.handler1 = new TestWebSocketHandler();
        this.handler1.setPath( "/ws1" );

        this.handler2 = new TestWebSocketHandler();
        this.handler2.setPath( "/ws2" );
    }

    @Test
    public void testAddBeforeActivate()
        throws Exception
    {
        assertEquals( 0, this.registrations.size() );

        this.manager.addHandler( this.handler1 );
        this.manager.addHandler( this.handler2 );

        this.manager.activate( this.context );
        assertEquals( 2, this.registrations.size() );

        this.manager.removeHandler( this.handler1 );
        this.manager.removeHandler( this.handler2 );

        assertEquals( 0, this.registrations.size() );
    }

    @Test
    public void testAddAfterActivate()
        throws Exception
    {
        assertEquals( 0, this.registrations.size() );
        this.manager.activate( this.context );
        assertEquals( 0, this.registrations.size() );

        this.manager.addHandler( this.handler1 );
        this.manager.addHandler( this.handler2 );
        assertEquals( 2, this.registrations.size() );

        this.manager.removeHandler( this.handler1 );
        this.manager.removeHandler( this.handler2 );

        assertEquals( 0, this.registrations.size() );
    }

    @Test
    public void testDeactivate()
        throws Exception
    {
        assertEquals( 0, this.registrations.size() );
        this.manager.activate( this.context );
        assertEquals( 0, this.registrations.size() );

        this.manager.addHandler( this.handler1 );
        this.manager.addHandler( this.handler2 );
        assertEquals( 2, this.registrations.size() );

        this.manager.deactivate();
        assertEquals( 0, this.registrations.size() );

        this.manager.activate( this.context );
        assertEquals( 2, this.registrations.size() );
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
        final Dictionary config = (Dictionary) invocation.getArguments()[2];
        final ServiceReference ref = mockServiceReference( config );

        final ServiceRegistration reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( reg.getReference() ).thenReturn( ref );

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
